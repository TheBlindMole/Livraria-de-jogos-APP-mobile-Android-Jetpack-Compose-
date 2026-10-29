package com.theblindmole.gamevault.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.repository.GameRepository
import com.theblindmole.gamevault.ui.common.WhileUiSubscribed
import com.theblindmole.gamevault.ui.navigation.GameDetailRoute
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface GameDetailUiState {
    data object Loading : GameDetailUiState
    data object NotFound : GameDetailUiState
    data object Deleted : GameDetailUiState
    data class Success(val game: Game) : GameDetailUiState
}

class GameDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val gameId = savedStateHandle.toRoute<GameDetailRoute>().gameId
    private val isDeleted = MutableStateFlow(false)

    val uiState: StateFlow<GameDetailUiState> = combine(
        gameRepository.observeGame(gameId),
        isDeleted,
    ) { game, deleted ->
        when {
            deleted -> GameDetailUiState.Deleted
            game == null -> GameDetailUiState.NotFound
            else -> GameDetailUiState.Success(game)
        }
    }.stateIn(viewModelScope, WhileUiSubscribed, GameDetailUiState.Loading)

    fun onToggleFavorite() {
        val game = (uiState.value as? GameDetailUiState.Success)?.game ?: return
        viewModelScope.launch { gameRepository.setFavorite(game.id, !game.isFavorite) }
    }

    fun onStatusChange(status: GameStatus) {
        viewModelScope.launch { gameRepository.setStatus(gameId, status) }
    }

    fun onRatingChange(rating: Int) {
        viewModelScope.launch { gameRepository.setRating(gameId, rating) }
    }

    //eliminacao concluida mesmo ao sair do ecra
    fun onDelete() {
        isDeleted.value = true
        viewModelScope.launch {
            withContext(NonCancellable) { gameRepository.delete(gameId) }
        }
    }
}
