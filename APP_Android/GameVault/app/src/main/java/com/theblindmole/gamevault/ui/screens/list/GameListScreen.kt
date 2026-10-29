package com.theblindmole.gamevault.ui.screens.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.model.SortOption
import com.theblindmole.gamevault.ui.AppViewModelProvider
import com.theblindmole.gamevault.ui.common.labelRes
import com.theblindmole.gamevault.ui.components.DropdownChip
import com.theblindmole.gamevault.ui.components.EmptyState
import com.theblindmole.gamevault.ui.components.GameCard
import com.theblindmole.gamevault.ui.components.LoadingIndicator

private val FabSpace = 88.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onGameClick: (Long) -> Unit,
    onAddGame: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: GameListViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onOpenStats) {
                        Icon(Icons.Outlined.BarChart, contentDescription = stringResource(R.string.stats_title))
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = stringResource(R.string.settings_title))
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddGame,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.action_add)) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        ) {
            SearchField(
                query = viewModel.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            FilterBar(
                uiState = uiState,
                onSortChange = viewModel::onSortChange,
                onFavoritesToggle = viewModel::onFavoritesOnlyToggle,
                onStatusChange = viewModel::onStatusFilterChange,
                onPlatformChange = viewModel::onPlatformFilterChange,
                onGenreChange = viewModel::onGenreFilterChange,
                onClearFilters = viewModel::onClearFilters,
            )
            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.totalCount == 0 -> EmptyState(
                    icon = Icons.Outlined.SportsEsports,
                    title = stringResource(R.string.empty_collection_title),
                    message = stringResource(R.string.empty_collection_message),
                    action = {
                        Button(onClick = onAddGame) { Text(stringResource(R.string.action_add_first)) }
                    },
                )
                uiState.games.isEmpty() -> EmptyState(
                    icon = Icons.Filled.Search,
                    title = stringResource(R.string.no_results_title),
                    message = stringResource(R.string.no_results_message),
                    action = {
                        TextButton(onClick = viewModel::onClearAll) { Text(stringResource(R.string.action_clear_filters)) }
                    },
                )
                else -> GameList(
                    games = uiState.games,
                    onGameClick = onGameClick,
                    onToggleFavorite = viewModel::onToggleFavorite,
                    bottomPadding = innerPadding.calculateBottomPadding(),
                )
            }
        }
    }
}

//campo de pesquisa por nome
@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_clear))
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.extraLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
    )
}

//barra de ordenacao e filtros
@Composable
private fun FilterBar(
    uiState: GameListUiState,
    onSortChange: (SortOption) -> Unit,
    onFavoritesToggle: () -> Unit,
    onStatusChange: (GameStatus?) -> Unit,
    onPlatformChange: (String?) -> Unit,
    onGenreChange: (String?) -> Unit,
    onClearFilters: () -> Unit,
) {
    val filter = uiState.filter
    val allPlatforms = stringResource(R.string.filter_all_platforms)
    val allGenres = stringResource(R.string.filter_all_genres)

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            DropdownChip(
                label = stringResource(uiState.sort.labelRes),
                options = SortOption.entries,
                selectedOption = uiState.sort,
                optionLabel = { stringResource(it.labelRes) },
                onOptionSelected = onSortChange,
                leadingIcon = Icons.AutoMirrored.Filled.Sort,
            )
        }
        item {
            FilterChip(
                selected = filter.favoritesOnly,
                onClick = onFavoritesToggle,
                label = { Text(stringResource(R.string.filter_favorites)) },
                leadingIcon = {
                    Icon(
                        imageVector = if (filter.favoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
            )
        }
        items(GameStatus.entries) { status ->
            FilterChip(
                selected = filter.status == status,
                onClick = { onStatusChange(if (filter.status == status) null else status) },
                label = { Text(stringResource(status.labelRes)) },
            )
        }
        if (uiState.platforms.isNotEmpty()) {
            item {
                DropdownChip(
                    label = filter.platform ?: stringResource(R.string.filter_platform),
                    options = listOf<String?>(null) + uiState.platforms,
                    selectedOption = filter.platform,
                    optionLabel = { it ?: allPlatforms },
                    onOptionSelected = onPlatformChange,
                    highlighted = filter.platform != null,
                )
            }
        }
        if (uiState.genres.isNotEmpty()) {
            item {
                DropdownChip(
                    label = filter.genre ?: stringResource(R.string.filter_genre),
                    options = listOf<String?>(null) + uiState.genres,
                    selectedOption = filter.genre,
                    optionLabel = { it ?: allGenres },
                    onOptionSelected = onGenreChange,
                    highlighted = filter.genre != null,
                )
            }
        }
        if (filter.hasActiveFilters) {
            item {
                AssistChip(
                    onClick = onClearFilters,
                    label = { Text(stringResource(R.string.action_clear_filters)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize),
                        )
                    },
                )
            }
        }
    }
}

//lista de jogos
@Composable
private fun GameList(
    games: List<Game>,
    onGameClick: (Long) -> Unit,
    onToggleFavorite: (Game) -> Unit,
    bottomPadding: Dp,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = bottomPadding + FabSpace,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = COUNT_KEY) {
            Text(
                text = pluralStringResource(R.plurals.games_count, games.size, games.size),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        items(games, key = { it.id }) { game ->
            GameCard(
                game = game,
                onClick = { onGameClick(game.id) },
                onToggleFavorite = { onToggleFavorite(game) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

private const val COUNT_KEY = "count"
