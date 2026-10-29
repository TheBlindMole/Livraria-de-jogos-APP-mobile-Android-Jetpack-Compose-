package com.theblindmole.gamevault.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theblindmole.gamevault.domain.model.CollectionStats
import com.theblindmole.gamevault.domain.repository.GameRepository
import com.theblindmole.gamevault.ui.common.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface StatsUiState {
    data object Loading : StatsUiState
    data class Ready(val stats: CollectionStats) : StatsUiState
}

class StatsViewModel(gameRepository: GameRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = gameRepository.observeGames()
        .map<_, StatsUiState> { StatsUiState.Ready(CollectionStats.from(it)) }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, WhileUiSubscribed, StatsUiState.Loading)
}
