package com.practicum.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.practicum.playlistmaker.screens.MainScreen
import com.practicum.playlistmaker.screens.SearchScreen
import com.practicum.playlistmaker.screens.SettingsScreen

@Composable
fun PlaylistHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.MAIN.name
    ) {
        composable(Screens.MAIN.name) {
            MainScreen(
                onSearchClick = { navigateToSearch(navController) },
                onSettingsClick = { navigateToSettings(navController) }
            )
        }

        composable(Screens.SEARCH.name) {
            SearchScreen(
                onBackClick = { navigateBack(navController) }
            )
        }

        composable(Screens.SETTINGS.name) {
            SettingsScreen(
                onBackClick = { navigateBack(navController) }
            )
        }
    }
}

// Функции навигации
private fun navigateToSearch(navController: NavHostController) {
    // Проверяем что мы не на экране поиска
    if (navController.currentDestination?.route != Screens.SEARCH.name) {
        navController.navigate(Screens.SEARCH.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToSettings(navController: NavHostController) {
    // Проверяем что мы не на экране настроек
    if (navController.currentDestination?.route != Screens.SETTINGS.name) {
        navController.navigate(Screens.SETTINGS.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateBack(navController: NavHostController) {
    // Проверяем что есть куда возвращаться
    if (navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }
}