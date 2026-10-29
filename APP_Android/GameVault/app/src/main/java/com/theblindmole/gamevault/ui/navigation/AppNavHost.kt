package com.theblindmole.gamevault.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.theblindmole.gamevault.ui.screens.detail.GameDetailScreen
import com.theblindmole.gamevault.ui.screens.form.GameFormScreen
import com.theblindmole.gamevault.ui.screens.list.GameListScreen
import com.theblindmole.gamevault.ui.screens.settings.SettingsScreen
import com.theblindmole.gamevault.ui.screens.stats.StatsScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = GameListRoute,
        modifier = modifier,
    ) {
        //lista principal
        composable<GameListRoute> {
            GameListScreen(
                onGameClick = { id -> navController.navigate(GameDetailRoute(id)) },
                onAddGame = { navController.navigate(GameFormRoute()) },
                onOpenStats = { navController.navigate(StatsRoute) },
                onOpenSettings = { navController.navigate(SettingsRoute) },
            )
        }

        //detalhe do jogo
        composable<GameDetailRoute> {
            GameDetailScreen(
                onBack = navController::navigateUp,
                onEdit = { id -> navController.navigate(GameFormRoute(id)) },
            )
        }

        //criacao e edicao
        composable<GameFormRoute> {
            GameFormScreen(
                onBack = navController::navigateUp,
                onSaved = navController::navigateUp,
            )
        }

        composable<StatsRoute> {
            StatsScreen(onBack = navController::navigateUp)
        }

        composable<SettingsRoute> {
            SettingsScreen(onBack = navController::navigateUp)
        }
    }
}
