package com.practicum.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.MainScreen
import com.practicum.playlistmaker.ui.screens.SearchScreen
import com.practicum.playlistmaker.ui.screens.SettingsScreen
import com.practicum.playlistmaker.ui.screens.FavoritesScreen
import com.practicum.playlistmaker.ui.screens.NewPlaylistScreen
import com.practicum.playlistmaker.ui.screens.PlaylistsScreen
import com.practicum.playlistmaker.ui.screens.TrackDetailsScreen

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
                onSettingsClick = { navigateToSettings(navController) },
                onPlaylistsClick = { navigateToPlaylists(navController) },
                onFavoritesClick = { navigateToFavorites(navController) }
            )
        }

        composable(Screens.SEARCH.name) {
            SearchScreen(
                onBackClick = { navigateBack(navController) },
                onTrackClick = { track -> navigateToTrackDetails(navController, track) }
            )
        }

        composable(Screens.SETTINGS.name) {
            SettingsScreen(
                onBackClick = { navigateBack(navController) }
            )
        }

        composable(Screens.PLAYLISTS.name) {
            PlaylistsScreen(
                onBackClick = { navigateBack(navController) },
                onAddNewPlaylist = { navigateToNewPlaylist(navController) },
                onPlaylistClick = { playlistId ->
                }
            )
        }

        composable(Screens.NEW_PLAYLIST.name) {
            NewPlaylistScreen(
                onBackClick = { navigateBack(navController) },
                onSaveClick = { navigateBack(navController) }
            )
        }

        composable(Screens.TRACK_DETAILS.name) {
            // Получаем трек из аргументов
            val track = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Track>("track")

            TrackDetailsScreen(
                track = track ?: Track("", "", ""), // Заглушка если трек не передан
                onBackClick = { navigateBack(navController) }
            )
        }

        composable(Screens.FAVORITES.name) {
            FavoritesScreen(
                onBackClick = { navigateBack(navController) },
                onTrackClick = { track -> navigateToTrackDetails(navController, track) }
            )
        }
    }
}

// Функции навигации
private fun navigateToSearch(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.SEARCH.name) {
        navController.navigate(Screens.SEARCH.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToSettings(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.SETTINGS.name) {
        navController.navigate(Screens.SETTINGS.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToPlaylists(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.PLAYLISTS.name) {
        navController.navigate(Screens.PLAYLISTS.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToFavorites(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.FAVORITES.name) {
        navController.navigate(Screens.FAVORITES.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToNewPlaylist(navController: NavHostController) {
    if (navController.currentDestination?.route != Screens.NEW_PLAYLIST.name) {
        navController.navigate(Screens.NEW_PLAYLIST.name) {
            launchSingleTop = true
            restoreState = true
        }
    }
}

private fun navigateToTrackDetails(navController: NavHostController, track: Track) {
    navController.currentBackStackEntry?.savedStateHandle?.set("track", track)

    navController.navigate(Screens.TRACK_DETAILS.name) {
        launchSingleTop = true
    }
}

private fun navigateBack(navController: NavHostController) {
    if (navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }
}