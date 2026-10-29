package com.theblindmole.gamevault.domain.repository

import com.theblindmole.gamevault.domain.model.SortOption
import com.theblindmole.gamevault.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val themeMode: Flow<ThemeMode>
    val sortOption: Flow<SortOption>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setSortOption(option: SortOption)
}
