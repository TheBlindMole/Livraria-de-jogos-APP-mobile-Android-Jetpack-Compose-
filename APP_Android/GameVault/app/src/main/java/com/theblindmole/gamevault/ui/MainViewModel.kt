package com.theblindmole.gamevault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theblindmole.gamevault.domain.model.ThemeMode
import com.theblindmole.gamevault.domain.repository.SettingsRepository
import com.theblindmole.gamevault.ui.common.WhileUiSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(settingsRepository: SettingsRepository) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, WhileUiSubscribed, ThemeMode.DARK)
}
