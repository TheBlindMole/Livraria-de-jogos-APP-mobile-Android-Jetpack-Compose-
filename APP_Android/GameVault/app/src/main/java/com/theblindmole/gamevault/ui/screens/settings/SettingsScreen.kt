package com.theblindmole.gamevault.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theblindmole.gamevault.BuildConfig
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.ThemeMode
import com.theblindmole.gamevault.ui.AppViewModelProvider
import com.theblindmole.gamevault.ui.common.labelRes
import com.theblindmole.gamevault.ui.components.BackButton
import com.theblindmole.gamevault.ui.components.FreeToGameAttribution
import com.theblindmole.gamevault.ui.components.InfoRow
import com.theblindmole.gamevault.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            //tema
            SectionCard(title = stringResource(R.string.settings_appearance)) {
                ThemeSelector(selected = themeMode, onSelect = viewModel::onThemeModeChange)
            }

            //informacao da aplicacao
            SectionCard(title = stringResource(R.string.settings_about)) {
                InfoRow(label = stringResource(R.string.about_version), value = BuildConfig.VERSION_NAME)
                InfoRow(label = stringResource(R.string.about_developer), value = BuildConfig.DEVELOPER)
                InfoRow(
                    label = stringResource(R.string.about_api),
                    value = stringResource(
                        if (viewModel.isOnlineSearchAvailable) R.string.api_configured else R.string.api_not_configured,
                    ),
                )
                FreeToGameAttribution(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
private fun ThemeSelector(selected: ThemeMode, onSelect: (ThemeMode) -> Unit) {
    Column(modifier = Modifier.selectableGroup()) {
        ThemeMode.entries.forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = mode == selected,
                        onClick = { onSelect(mode) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RadioButton(selected = mode == selected, onClick = null)
                Text(text = stringResource(mode.labelRes), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
