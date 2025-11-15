package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.playlists.PlaylistViewModel
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackDetailsScreen(
    track: Track,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModel.getViewModelFactory()
    )

    // Локальное состояние трека для рендера
    var displayedTrack by remember { mutableStateOf(track) }

    // Состояние избранного
    var isFavorite by remember { mutableStateOf(displayedTrack.favorite) }

    // Состояние BottomSheet
    var showPlaylistSheet by remember { mutableStateOf(false) }

    // Подписка на базу
    val currentTrackState by viewModel.isTrackInPlaylist(displayedTrack).collectAsState(initial = null)
    val playlists by viewModel.playlists.collectAsState()

    // Синхронизация с базой
    LaunchedEffect(currentTrackState) {
        currentTrackState?.let {
            displayedTrack = it
            isFavorite = it.favorite
        }
    }

    // Сохраняем трек в базу при первом открытии
    LaunchedEffect(displayedTrack) {
        if (currentTrackState == null) {
            viewModel.saveTrackToDatabase(displayedTrack)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(color = AppColors.white, shape = RoundedCornerShape(0.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_music),
                        contentDescription = displayedTrack.trackName,
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 24.dp),
                        tint = AppColors.gray
                    )

                    Text(
                        text = displayedTrack.trackName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = displayedTrack.artistName,
                        fontSize = 18.sp,
                        color = AppColors.primaryBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = displayedTrack.trackTime,
                        fontSize = 16.sp,
                        color = AppColors.gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                val newFavorite = !isFavorite
                                isFavorite = newFavorite
                                viewModel.toggleFavorite(displayedTrack, newFavorite)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = stringResource(R.string.add_to_favorites),
                                modifier = Modifier.size(32.dp),
                                tint = if (isFavorite) AppColors.red else AppColors.gray
                            )
                            Text(
                                text = stringResource(R.string.favorite),
                                fontSize = 12.sp,
                                color = AppColors.black,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showPlaylistSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                contentDescription = stringResource(R.string.add_to_playlist),
                                modifier = Modifier.size(32.dp),
                                tint = AppColors.gray
                            )
                            Text(
                                text = stringResource(R.string.playlist),
                                fontSize = 12.sp,
                                color = AppColors.black,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.white),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.track_details_title),
                    color = AppColors.black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                )
            }
        }

        // BottomSheet для выбора плейлиста
        if (showPlaylistSheet) {
            PlaylistSelectionBottomSheet(
                playlists = playlists,
                onPlaylistSelected = { playlist ->
                    viewModel.addTrackToPlaylist(displayedTrack, playlist.id)
                    showPlaylistSheet = false
                },
                onDismiss = { showPlaylistSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistSelectionBottomSheet(
    playlists: List<com.practicum.playlistmaker.domain.models.Playlist>,
    onPlaylistSelected: (com.practicum.playlistmaker.domain.models.Playlist) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.select_playlist),
                style = MaterialTheme.typography.headlineSmall,
                color = AppColors.black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (playlists.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_playlists),
                        color = AppColors.gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(playlists) { playlist ->
                        PlaylistBottomSheetItem(
                            playlist = playlist,
                            onClick = { onPlaylistSelected(playlist) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistBottomSheetItem(
    playlist: com.practicum.playlistmaker.domain.models.Playlist,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(AppColors.lightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = null,
                tint = AppColors.gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                ),
                color = AppColors.black
            )

            if (playlist.description.isNotEmpty()) {
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 18.sp
                    ),
                    color = AppColors.gray,
                    maxLines = 1
                )
            }

            Text(
                text = "${playlist.tracks.size} ${stringResource(R.string.tracks_count)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    lineHeight = 16.sp
                ),
                color = AppColors.gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackDetailsPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            TrackDetailsScreen(
                track = Track("Bohemian Rhapsody", "Queen", "05:55"),
                onBackClick = {}
            )
        }
    }
}