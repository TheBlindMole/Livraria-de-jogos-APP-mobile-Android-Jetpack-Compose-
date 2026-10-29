package com.theblindmole.gamevault.ui.screens.form

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.validation.GameValidator
import com.theblindmole.gamevault.domain.validation.ValidationError
import com.theblindmole.gamevault.ui.AppViewModelProvider
import com.theblindmole.gamevault.ui.common.asText
import com.theblindmole.gamevault.ui.components.BackButton
import com.theblindmole.gamevault.ui.components.GameCover
import com.theblindmole.gamevault.ui.components.LoadingIndicator
import com.theblindmole.gamevault.ui.components.RatingBar
import com.theblindmole.gamevault.ui.components.StatusSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: GameFormViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState = viewModel.uiState
    val searchState = viewModel.searchState
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val currentOnSaved by rememberUpdatedState(onSaved)
    val currentOnBack by rememberUpdatedState(onBack)

    //eventos do viewmodel
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GameFormEvent.Saved -> currentOnSaved()
                GameFormEvent.NotFound -> currentOnBack()
                is GameFormEvent.ShowMessage -> snackbarHostState.showSnackbar(context.getString(event.messageRes))
            }
        }
    }

    //selecao de imagem da galeria
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.onCoverPicked(uri.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (uiState.isEditing) R.string.form_title_edit else R.string.form_title_new))
                },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)

        if (uiState.isLoading) {
            LoadingIndicator(contentModifier)
        } else {
            GameFormContent(
                uiState = uiState,
                actions = viewModel,
                onPickCover = {
                    coverPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = contentModifier,
            )
        }
    }

    if (searchState.isVisible) {
        RemoteSearchSheet(
            state = searchState,
            onQueryChange = viewModel::onSearchQueryChange,
            onRetry = viewModel::onRetrySearch,
            onGameSelected = viewModel::onRemoteGameSelected,
            onDismiss = viewModel::onDismissSearch,
        )
    }
}

@Composable
private fun GameFormContent(
    uiState: GameFormUiState,
    actions: GameFormActions,
    onPickCover: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val form = uiState.form

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedButton(onClick = actions::onOpenSearch, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.form_search_online))
        }

        CoverPicker(
            coverUrl = form.coverUrl,
            isImporting = uiState.isImportingCover,
            onPick = onPickCover,
            onRemove = actions::onRemoveCover,
        )

        //dados principais
        FormTextField(
            value = form.name,
            onValueChange = actions::onNameChange,
            label = stringResource(R.string.field_name),
            error = form.nameError,
        )
        FormTextField(
            value = form.platform,
            onValueChange = actions::onPlatformChange,
            label = stringResource(R.string.field_platform),
            error = form.platformError,
        )
        SuggestionRow(
            suggestions = form.platformSuggestions,
            selected = form.platform,
            onSelect = actions::onPlatformChange,
        )
        FormTextField(
            value = form.genre,
            onValueChange = actions::onGenreChange,
            label = stringResource(R.string.field_genre),
            error = form.genreError,
        )
        SuggestionRow(
            suggestions = form.genreSuggestions,
            selected = form.genre,
            onSelect = actions::onGenreChange,
        )
        FormTextField(
            value = form.releaseYear,
            onValueChange = actions::onYearChange,
            label = stringResource(R.string.field_year),
            error = form.yearError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        )

        //estado e avaliacao
        FieldLabel(stringResource(R.string.field_status))
        StatusSelector(selected = form.status, onSelect = actions::onStatusChange)

        FieldLabel(stringResource(R.string.field_rating))
        RatingBar(rating = form.rating, starSize = 32.dp, onRatingChange = actions::onRatingChange)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.field_favorite),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = form.isFavorite, onCheckedChange = actions::onFavoriteChange)
        }

        OutlinedTextField(
            value = form.notes,
            onValueChange = actions::onNotesChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.field_notes)) },
            minLines = 3,
            supportingText = {
                Text(stringResource(R.string.notes_counter, form.notes.length, GameValidator.NOTES_MAX_LENGTH))
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )

        Button(
            onClick = actions::onSave,
            enabled = !uiState.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

//pre visualizacao e escolha da capa
@Composable
private fun CoverPicker(
    coverUrl: String?,
    isImporting: Boolean,
    onPick: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(modifier = Modifier.size(width = 144.dp, height = 96.dp), contentAlignment = Alignment.Center) {
            GameCover(
                coverUrl = coverUrl,
                contentDescription = stringResource(R.string.field_cover),
                modifier = Modifier.fillMaxSize(),
            )
            if (isImporting) CircularProgressIndicator()
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            OutlinedButton(onClick = onPick, enabled = !isImporting) {
                Icon(
                    imageVector = Icons.Outlined.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.cover_pick))
            }
            if (coverUrl != null) {
                TextButton(onClick = onRemove) { Text(stringResource(R.string.cover_remove)) }
            }
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: ValidationError?,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        capitalization = KeyboardCapitalization.Words,
        imeAction = ImeAction.Next,
    ),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error.asText()) }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
    )
}

//sugestoes vindas da api
@Composable
private fun SuggestionRow(
    suggestions: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
) {
    if (suggestions.size <= 1) return

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.form_suggestions),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { suggestion ->
                FilterChip(
                    selected = suggestion.equals(selected.trim(), ignoreCase = true),
                    onClick = { onSelect(suggestion) },
                    label = { Text(suggestion) },
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleSmall)
}
