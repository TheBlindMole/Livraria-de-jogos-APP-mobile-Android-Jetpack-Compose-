package com.theblindmole.gamevault.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.ui.AppViewModelProvider
import com.theblindmole.gamevault.ui.components.BackButton
import com.theblindmole.gamevault.ui.components.ConfirmDialog
import com.theblindmole.gamevault.ui.components.EmptyState
import com.theblindmole.gamevault.ui.components.FavoriteButton
import com.theblindmole.gamevault.ui.components.GameCover
import com.theblindmole.gamevault.ui.components.InfoRow
import com.theblindmole.gamevault.ui.components.LoadingIndicator
import com.theblindmole.gamevault.ui.components.RatingBar
import com.theblindmole.gamevault.ui.components.SectionCard
import com.theblindmole.gamevault.ui.components.StatusSelector
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val COVER_RATIO = 16f / 9f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: GameDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val game = (uiState as? GameDetailUiState.Success)?.game

    //volta atras depois de eliminar
    LaunchedEffect(uiState) {
        if (uiState is GameDetailUiState.Deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onClick = onBack) },
                actions = {
                    if (game != null) {
                        FavoriteButton(isFavorite = game.isFavorite, onClick = viewModel::onToggleFavorite)
                        IconButton(onClick = { onEdit(game.id) }) {
                            Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            GameDetailUiState.Loading, GameDetailUiState.Deleted -> LoadingIndicator(contentModifier)
            GameDetailUiState.NotFound -> EmptyState(
                icon = Icons.Outlined.SportsEsports,
                title = stringResource(R.string.game_not_found),
                message = stringResource(R.string.game_not_found_message),
                modifier = contentModifier,
            )
            is GameDetailUiState.Success -> GameDetailContent(
                game = state.game,
                onStatusChange = viewModel::onStatusChange,
                onRatingChange = viewModel::onRatingChange,
                modifier = contentModifier,
            )
        }
    }

    if (showDeleteDialog && game != null) {
        ConfirmDialog(
            title = stringResource(R.string.delete_title),
            message = stringResource(R.string.delete_message, game.name),
            confirmLabel = stringResource(R.string.action_delete),
            onConfirm = {
                showDeleteDialog = false
                viewModel.onDelete()
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Composable
private fun GameDetailContent(
    game: Game,
    onStatusChange: (GameStatus) -> Unit,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val addedOn = remember(game.createdAt) { formatDate(game.createdAt) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GameCover(
            coverUrl = game.coverUrl,
            contentDescription = game.name,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(COVER_RATIO),
        )

        //cabecalho
        Column {
            Text(text = game.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = stringResource(R.string.added_on, addedOn),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        //informacao principal
        SectionCard(title = stringResource(R.string.detail_info)) {
            InfoRow(label = stringResource(R.string.field_platform), value = game.platform)
            InfoRow(label = stringResource(R.string.field_genre), value = game.genre)
            InfoRow(label = stringResource(R.string.field_year), value = game.releaseYear.toString())
        }

        SectionCard(title = stringResource(R.string.field_status)) {
            StatusSelector(selected = game.status, onSelect = onStatusChange)
        }

        SectionCard(title = stringResource(R.string.field_rating)) {
            RatingBar(rating = game.rating, starSize = 32.dp, onRatingChange = onRatingChange)
        }

        if (game.notes.isNotBlank()) {
            SectionCard(title = stringResource(R.string.field_notes)) {
                Text(text = game.notes, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
