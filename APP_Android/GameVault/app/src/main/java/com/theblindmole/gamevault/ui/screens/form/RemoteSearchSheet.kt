package com.theblindmole.gamevault.ui.screens.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.RemoteGame
import com.theblindmole.gamevault.domain.model.SearchError
import com.theblindmole.gamevault.ui.common.messageRes
import com.theblindmole.gamevault.ui.components.FreeToGameAttribution
import com.theblindmole.gamevault.ui.components.GameCover

private const val SHEET_HEIGHT_FRACTION = 0.9f
private const val MAX_PLATFORMS_SHOWN = 3

//pesquisa de jogos na api freetogame
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteSearchSheet(
    state: RemoteSearchUiState,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onGameSelected: (RemoteGame) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (state.query.isEmpty()) focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(SHEET_HEIGHT_FRACTION)
                .padding(horizontal = 16.dp),
        ) {
            Text(text = stringResource(R.string.search_title), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_clear))
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when (val status = state.status) {
                    RemoteSearchStatus.Idle -> CenteredMessage(stringResource(R.string.search_hint))
                    RemoteSearchStatus.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    RemoteSearchStatus.Empty -> CenteredMessage(stringResource(R.string.search_empty))
                    is RemoteSearchStatus.Error -> SearchErrorMessage(error = status.error, onRetry = onRetry)
                    is RemoteSearchStatus.Results -> LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(status.games, key = { it.id }) { game ->
                            RemoteGameItem(game = game, onClick = { onGameSelected(game) })
                        }
                    }
                }
            }

            FreeToGameAttribution(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
private fun RemoteGameItem(game: RemoteGame, onClick: () -> Unit) {
    val details = listOfNotNull(
        game.releaseYear?.toString(),
        game.platforms.take(MAX_PLATFORMS_SHOWN).joinToString().ifEmpty { null },
    ).joinToString(stringResource(R.string.list_separator))

    ListItem(
        headlineContent = { Text(game.name, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        supportingContent = { Text(details, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingContent = {
            GameCover(
                coverUrl = game.coverUrl,
                contentDescription = null,
                modifier = Modifier.size(width = 72.dp, height = 52.dp),
                shape = MaterialTheme.shapes.small,
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun SearchErrorMessage(error: SearchError, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(error.messageRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    )
}
