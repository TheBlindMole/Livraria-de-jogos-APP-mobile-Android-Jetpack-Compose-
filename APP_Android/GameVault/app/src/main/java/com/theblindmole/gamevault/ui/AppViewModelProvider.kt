package com.theblindmole.gamevault.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.theblindmole.gamevault.GameVaultApp
import com.theblindmole.gamevault.di.AppContainer
import com.theblindmole.gamevault.ui.screens.detail.GameDetailViewModel
import com.theblindmole.gamevault.ui.screens.form.GameFormViewModel
import com.theblindmole.gamevault.ui.screens.list.GameListViewModel
import com.theblindmole.gamevault.ui.screens.settings.SettingsViewModel
import com.theblindmole.gamevault.ui.screens.stats.StatsViewModel

//fabrica de viewmodels da aplicacao
object AppViewModelProvider {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            MainViewModel(container().settingsRepository)
        }
        initializer {
            GameListViewModel(container().gameRepository, container().settingsRepository)
        }
        initializer {
            GameDetailViewModel(createSavedStateHandle(), container().gameRepository)
        }
        initializer {
            GameFormViewModel(
                createSavedStateHandle(),
                container().gameRepository,
                container().gameSearchRepository,
            )
        }
        initializer {
            StatsViewModel(container().gameRepository)
        }
        initializer {
            SettingsViewModel(container().settingsRepository, container().gameSearchRepository)
        }
    }

    private fun CreationExtras.container(): AppContainer =
        (checkNotNull(this[APPLICATION_KEY]) as GameVaultApp).container
}
