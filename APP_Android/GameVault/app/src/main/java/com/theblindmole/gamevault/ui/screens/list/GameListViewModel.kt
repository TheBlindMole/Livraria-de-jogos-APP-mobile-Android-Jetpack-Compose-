package com.theblindmole.gamevault.ui.screens.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameFilter
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.model.SortOption
import com.theblindmole.gamevault.domain.model.distinctValues
import com.theblindmole.gamevault.domain.model.filterAndSort
import com.theblindmole.gamevault.domain.repository.GameRepository
import com.theblindmole.gamevault.domain.repository.SettingsRepository
import com.theblindmole.gamevault.ui.common.WhileUiSubscribed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameListUiState(
    val isLoading: Boolean = true,
    val games: List<Game> = emptyList(),
    val totalCount: Int = 0,
    val filter: GameFilter = GameFilter(),
    val sort: SortOption = SortOption.RECENT,
    val platforms: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
)

class GameListViewModel(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    private val filter = MutableStateFlow(GameFilter())

    val uiState: StateFlow<GameListUiState> = combine(
        gameRepository.observeGames(),
        snapshotFlow { searchQuery },
        filter,
        settingsRepository.sortOption,
    ) { games, query, currentFilter, sort ->
        val activeFilter = currentFilter.copy(query = query)
        GameListUiState(
            isLoading = false,
            games = games.filterAndSort(activeFilter, sort),
            totalCount = games.size,
            filter = activeFilter,
            sort = sort,
            platforms = games.distinctValues { it.platform },
            genres = games.distinctValues { it.genre },
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, WhileUiSubscribed, GameListUiState())

    //pesquisa e filtros
    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun onStatusFilterChange(status: GameStatus?) {
        filter.update { it.copy(status = status) }
    }

    fun onPlatformFilterChange(platform: String?) {
        filter.update { it.copy(platform = platform) }
    }

    fun onGenreFilterChange(genre: String?) {
        filter.update { it.copy(genre = genre) }
    }

    fun onFavoritesOnlyToggle() {
        filter.update { it.copy(favoritesOnly = !it.favoritesOnly) }
    }

    fun onClearFilters() {
        filter.value = GameFilter()
    }

    fun onClearAll() {
        searchQuery = ""
        onClearFilters()
    }

    //ordenacao guardada nas preferencias
    fun onSortChange(sort: SortOption) {
        viewModelScope.launch { settingsRepository.setSortOption(sort) }
    }

    fun onToggleFavorite(game: Game) {
        viewModelScope.launch { gameRepository.setFavorite(game.id, !game.isFavorite) }
    }
}
