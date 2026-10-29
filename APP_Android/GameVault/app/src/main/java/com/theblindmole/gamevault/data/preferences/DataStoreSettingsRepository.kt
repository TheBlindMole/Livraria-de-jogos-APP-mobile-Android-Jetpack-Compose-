package com.theblindmole.gamevault.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.theblindmole.gamevault.domain.model.SortOption
import com.theblindmole.gamevault.domain.model.ThemeMode
import com.theblindmole.gamevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreSettingsRepository(context: Context) : SettingsRepository {

    private val dataStore = context.settingsDataStore

    private val preferences: Flow<Preferences> = dataStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    override val themeMode: Flow<ThemeMode> = preferences
        .map { it[THEME_MODE].toEnumOrDefault(ThemeMode.DARK) }
        .distinctUntilChanged()

    override val sortOption: Flow<SortOption> = preferences
        .map { it[SORT_OPTION].toEnumOrDefault(SortOption.RECENT) }
        .distinctUntilChanged()

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    override suspend fun setSortOption(option: SortOption) {
        dataStore.edit { it[SORT_OPTION] = option.name }
    }

    private companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SORT_OPTION = stringPreferencesKey("sort_option")
    }
}

private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(default: T): T =
    this?.let { name -> enumValues<T>().firstOrNull { it.name == name } } ?: default
