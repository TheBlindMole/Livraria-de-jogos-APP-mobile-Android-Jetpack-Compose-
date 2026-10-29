package com.theblindmole.gamevault.ui.screens.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.model.RemoteGame
import com.theblindmole.gamevault.domain.model.SearchResult
import com.theblindmole.gamevault.domain.repository.GameRepository
import com.theblindmole.gamevault.domain.repository.GameSearchRepository
import com.theblindmole.gamevault.domain.validation.GameValidator
import com.theblindmole.gamevault.domain.validation.ValidationError
import com.theblindmole.gamevault.ui.navigation.GameFormRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class GameFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val searchRepository: GameSearchRepository,
) : ViewModel(), GameFormActions {

    private val gameId = savedStateHandle.toRoute<GameFormRoute>().gameId
    private val isEditing = gameId != GameFormRoute.NEW_GAME
    private var original: Game? = null
    private var searchJob: Job? = null

    var uiState by mutableStateOf(GameFormUiState(isEditing = isEditing, isLoading = isEditing))
        private set

    var searchState by mutableStateOf(RemoteSearchUiState())
        private set

    private val eventChannel = Channel<GameFormEvent>(Channel.BUFFERED)
    val events: Flow<GameFormEvent> = eventChannel.receiveAsFlow()

    init {
        if (isEditing) loadGame()
    }

    private fun loadGame() {
        viewModelScope.launch {
            val game = gameRepository.getGame(gameId)
            if (game == null) {
                eventChannel.send(GameFormEvent.NotFound)
                return@launch
            }
            original = game
            uiState = uiState.copy(isLoading = false, form = GameForm.from(game))
        }
    }

    //edicao dos campos
    override fun onNameChange(value: String) = updateForm {
        it.copy(name = value, nameError = revalidate(it.nameError) { GameValidator.validateName(value) })
    }

    override fun onPlatformChange(value: String) = updateForm {
        it.copy(platform = value, platformError = revalidate(it.platformError) { GameValidator.validateField(value) })
    }

    override fun onGenreChange(value: String) = updateForm {
        it.copy(genre = value, genreError = revalidate(it.genreError) { GameValidator.validateField(value) })
    }

    override fun onYearChange(value: String) {
        val digits = value.filter(Char::isDigit).take(GameValidator.YEAR_LENGTH)
        updateForm {
            it.copy(releaseYear = digits, yearError = revalidate(it.yearError) { GameValidator.validateYear(digits) })
        }
    }

    override fun onStatusChange(status: GameStatus) = updateForm { it.copy(status = status) }

    override fun onRatingChange(rating: Int) = updateForm { it.copy(rating = rating) }

    override fun onFavoriteChange(favorite: Boolean) = updateForm { it.copy(isFavorite = favorite) }

    override fun onNotesChange(value: String) = updateForm {
        it.copy(notes = value.take(GameValidator.NOTES_MAX_LENGTH))
    }

    //capa
    fun onCoverPicked(uri: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isImportingCover = true)
            val localCover = gameRepository.importCover(uri)
            uiState = uiState.copy(isImportingCover = false)
            if (localCover != null) {
                updateForm { it.copy(coverUrl = localCover) }
            } else {
                eventChannel.send(GameFormEvent.ShowMessage(R.string.error_cover_import))
            }
        }
    }

    override fun onRemoveCover() = updateForm { it.copy(coverUrl = null) }

    //pesquisa online
    override fun onOpenSearch() {
        searchState = RemoteSearchUiState(isVisible = true)
        onSearchQueryChange(uiState.form.name)
    }

    fun onDismissSearch() {
        searchJob?.cancel()
        searchState = searchState.copy(isVisible = false)
    }

    fun onSearchQueryChange(query: String) {
        searchState = searchState.copy(query = query)
        searchJob?.cancel()

        if (query.trim().length < MIN_QUERY_LENGTH) {
            searchState = searchState.copy(status = RemoteSearchStatus.Idle)
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MILLIS)
            searchState = searchState.copy(status = RemoteSearchStatus.Loading)
            val status = when (val result = searchRepository.search(query)) {
                is SearchResult.Success ->
                    if (result.games.isEmpty()) RemoteSearchStatus.Empty else RemoteSearchStatus.Results(result.games)
                is SearchResult.Failure -> RemoteSearchStatus.Error(result.error)
            }
            searchState = searchState.copy(status = status)
        }
    }

    fun onRetrySearch() = onSearchQueryChange(searchState.query)

    fun onRemoteGameSelected(remote: RemoteGame) {
        updateForm { it.withRemoteGame(remote) }
        onDismissSearch()
    }

    //validacao e gravacao
    override fun onSave() {
        if (uiState.isSaving || uiState.isLoading) return

        val form = uiState.form.validated()
        if (form.hasErrors) {
            uiState = uiState.copy(form = form)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            if (gameRepository.isDuplicate(form.name.trim(), form.platform.trim(), gameId)) {
                uiState = uiState.copy(isSaving = false)
                eventChannel.send(GameFormEvent.ShowMessage(R.string.error_duplicate))
                return@launch
            }
            gameRepository.save(form.toGame(id = gameId, original = original))
            eventChannel.send(GameFormEvent.Saved)
        }
    }

    private inline fun updateForm(transform: (GameForm) -> GameForm) {
        uiState = uiState.copy(form = transform(uiState.form))
    }

    //so revalida campos que ja tinham erro
    private inline fun revalidate(
        currentError: ValidationError?,
        validation: () -> ValidationError?,
    ): ValidationError? = if (currentError != null) validation() else null

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val SEARCH_DEBOUNCE_MILLIS = 400L
    }
}
