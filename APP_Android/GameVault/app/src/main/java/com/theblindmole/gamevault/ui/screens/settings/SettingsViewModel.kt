package com.theblindmole.gamevault.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theblindmole.gamevault.domain.model.ThemeMode
import com.theblindmole.gamevault.domain.repository.GameSearchRepository
import com.theblindmole.gamevault.domain.repository.SettingsRepository
import com.theblindmole.gamevault.ui.common.WhileUiSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    searchRepository: GameSearchRepository,
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, WhileUiSubscribed, ThemeMode.DARK)

    val isOnlineSearchAvailable: Boolean = searchRepository.isAvailable

    fun onThemeModeChange(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }
}
