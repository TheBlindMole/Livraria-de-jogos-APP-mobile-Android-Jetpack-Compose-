package com.theblindmole.gamevault.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object GameListRoute

@Serializable
data class GameDetailRoute(val gameId: Long)

@Serializable
data class GameFormRoute(val gameId: Long = NEW_GAME) {
    companion object {
        const val NEW_GAME = 0L
    }
}

@Serializable
data object StatsRoute

@Serializable
data object SettingsRoute
