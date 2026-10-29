package com.theblindmole.gamevault.ui.screens.form

import androidx.annotation.StringRes
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.model.RemoteGame
import com.theblindmole.gamevault.domain.model.SearchError
import com.theblindmole.gamevault.domain.validation.GameValidator
import com.theblindmole.gamevault.domain.validation.ValidationError

//campos do formulario
data class GameForm(
    val name: String = "",
    val platform: String = "",
    val genre: String = "",
    val releaseYear: String = "",
    val status: GameStatus = GameStatus.TO_PLAY,
    val rating: Int = 0,
    val notes: String = "",
    val isFavorite: Boolean = false,
    val coverUrl: String? = null,
    val rawgId: Int? = null,
    val platformSuggestions: List<String> = emptyList(),
    val genreSuggestions: List<String> = emptyList(),
    val nameError: ValidationError? = null,
    val platformError: ValidationError? = null,
    val genreError: ValidationError? = null,
    val yearError: ValidationError? = null,
) {
    val hasErrors: Boolean
        get() = listOf(nameError, platformError, genreError, yearError).any { it != null }

    fun validated(): GameForm = copy(
        nameError = GameValidator.validateName(name),
        platformError = GameValidator.validateField(platform),
        genreError = GameValidator.validateField(genre),
        yearError = GameValidator.validateYear(releaseYear),
    )

    //preenche com os dados da api
    fun withRemoteGame(remote: RemoteGame): GameForm = copy(
        name = remote.name,
        platform = remote.platforms.firstOrNull { it.equals(platform.trim(), ignoreCase = true) }
            ?: remote.platforms.firstOrNull()
            ?: platform,
        genre = remote.genres.firstOrNull() ?: genre,
        releaseYear = remote.releaseYear?.toString() ?: releaseYear,
        coverUrl = remote.coverUrl ?: coverUrl,
        rawgId = remote.id,
        platformSuggestions = remote.platforms,
        genreSuggestions = remote.genres,
        nameError = null,
        platformError = null,
        genreError = null,
        yearError = null,
    )

    fun toGame(id: Long, original: Game?): Game = Game(
        id = id,
        name = name.trim(),
        platform = platform.trim(),
        genre = genre.trim(),
        releaseYear = releaseYear.trim().toInt(),
        status = status,
        isFavorite = isFavorite,
        rating = rating,
        notes = notes.trim(),
        coverUrl = coverUrl,
        rawgId = rawgId,
        createdAt = original?.createdAt ?: System.currentTimeMillis(),
    )

    companion object {
        fun from(game: Game): GameForm = GameForm(
            name = game.name,
            platform = game.platform,
            genre = game.genre,
            releaseYear = game.releaseYear.toString(),
            status = game.status,
            rating = game.rating,
            notes = game.notes,
            isFavorite = game.isFavorite,
            coverUrl = game.coverUrl,
            rawgId = game.rawgId,
        )
    }
}

data class GameFormUiState(
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isImportingCover: Boolean = false,
    val form: GameForm = GameForm(),
)

//estado da pesquisa online
data class RemoteSearchUiState(
    val isVisible: Boolean = false,
    val query: String = "",
    val status: RemoteSearchStatus = RemoteSearchStatus.Idle,
)

sealed interface RemoteSearchStatus {
    data object Idle : RemoteSearchStatus
    data object Loading : RemoteSearchStatus
    data object Empty : RemoteSearchStatus
    data class Results(val games: List<RemoteGame>) : RemoteSearchStatus
    data class Error(val error: SearchError) : RemoteSearchStatus
}

sealed interface GameFormEvent {
    data object Saved : GameFormEvent
    data object NotFound : GameFormEvent
    data class ShowMessage(@StringRes val messageRes: Int) : GameFormEvent
}

//acoes do formulario expostas ao ecra
interface GameFormActions {
    fun onNameChange(value: String)
    fun onPlatformChange(value: String)
    fun onGenreChange(value: String)
    fun onYearChange(value: String)
    fun onStatusChange(status: GameStatus)
    fun onRatingChange(rating: Int)
    fun onFavoriteChange(favorite: Boolean)
    fun onNotesChange(value: String)
    fun onRemoveCover()
    fun onOpenSearch()
    fun onSave()
}
