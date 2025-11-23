package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.screens.playlists.PlaylistViewModel
import com.practicum.playlistmaker.ui.screens.search.components.TrackListItem
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlistId: Long,
    onBackClick: () -> Unit,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModel.getViewModelFactory()
    )

    var showMenu by remember { mutableStateOf(false) }
    var showDeletePlaylistDialog by remember { mutableStateOf(false) }
    var showDeleteTrackDialog by remember { mutableStateOf(false) }
    var selectedTrack by remember { mutableStateOf<Track?>(null) }

    val playlists by viewModel.playlists.collectAsState()
    val playlist = playlists.firstOrNull { it.id == playlistId }
    val tracks = playlist?.tracks ?: emptyList()
    val totalMinutes = tracks.sumOf { track ->
        track.trackTime.split(":").let { (min, _) -> min.toIntOrNull() ?: 0 }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(AppColors.white)
        ) {
            playlist?.let { pl ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Картинка плейлиста
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = pl.name,
                        modifier = Modifier
                            .size(312.dp)
                            .padding(bottom = 16.dp),
                        tint = AppColors.gray
                    )

                    // Название
                    Text(
                        text = pl.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Описание
                    Text(
                        text = pl.description.ifEmpty { stringResource(R.string.no_description) },
                        fontSize = 18.sp,
                        color = AppColors.black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Количество треков и общая длительность
                    Text(
                        text = stringResource(
                            id = R.string.playlist_tracks_info,
                            totalMinutes, tracks.size
                        ),
                        fontSize = 18.sp,
                        color = AppColors.black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Кнопка три точки слева
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { showMenu = true },
                        tint = AppColors.black
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Список треков
            if (tracks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_playlist_track),
                        fontSize = 16.sp,
                        color = AppColors.gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tracks) { track ->
                        TrackListItem(
                            track = track,
                            modifier = Modifier.combinedClickable(
                                onClick = { onTrackClick(track) },
                                onLongClick = {
                                    selectedTrack = track
                                    showDeleteTrackDialog = true
                                }
                            )
                        )
                    }
                }



            }
        }

        // Верхняя панель с кнопкой назад
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
                    modifier = Modifier.size(32.dp).clickable { onBackClick() },
                    tint = AppColors.black
                )
            }
        }

        // BottomSheet меню
        if (showMenu && playlist != null) {
            ModalBottomSheet(
                onDismissRequest = { showMenu = false },
                containerColor = AppColors.white
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = playlist.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = AppColors.black
                    )
                    Text(
                        text = stringResource(
                            id = R.string.playlist_tracks_info,
                            tracks.size,
                            totalMinutes
                        ),
                        fontSize = 14.sp,
                        color = AppColors.gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    HorizontalDivider(thickness = 1.dp, color = AppColors.gray)

                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = stringResource(R.string.share),
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Поделиться */ showMenu = false }
                                .padding(8.dp)
                        )
                        Text(
                            text = stringResource(R.string.edit_playlist_info),
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { /* Редактировать */ showMenu = false }
                                .padding(8.dp)
                        )
                        Text(
                            text = stringResource(R.string.delete_playlist),
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDeletePlaylistDialog = true
                                    showMenu = false
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        // Диалог удаления плейлиста
        if (showDeletePlaylistDialog && playlist != null) {
            AlertDialog(
                onDismissRequest = { showDeletePlaylistDialog = false },
                title = { Text(text = stringResource(R.string.delete_playlist)) },
                text = { Text(text = stringResource(R.string.delete_playlist_confirm)) },
                confirmButton = {
                    Text(
                        text = stringResource(R.string.yes),
                        modifier = Modifier
                            .clickable {
                                viewModel.deletePlaylistById(playlist.id)
                                showDeletePlaylistDialog = false
                                onBackClick()
                            }
                            .padding(8.dp)
                    )
                },
                dismissButton = {
                    Text(
                        text = stringResource(R.string.no),
                        modifier = Modifier
                            .clickable { showDeletePlaylistDialog = false }
                            .padding(8.dp)
                    )
                }
            )
        }

        // Диалог удаления трека
        if (showDeleteTrackDialog && selectedTrack != null) {
            AlertDialog(
                onDismissRequest = { showDeleteTrackDialog = false },
                title = { Text(text = stringResource(R.string.delete_track)) },
                text = { Text(text = stringResource(R.string.delete_track_confirm)) },
                confirmButton = {
                    Text(
                        text = stringResource(R.string.yes),
                        modifier = Modifier
                            .clickable {
                                viewModel.deleteSongFromPlaylist(selectedTrack!!)
                                showDeleteTrackDialog = false
                            }
                            .padding(8.dp)
                    )
                },
                dismissButton = {
                    Text(
                        text = stringResource(R.string.no),
                        modifier = Modifier
                            .clickable { showDeleteTrackDialog = false }
                            .padding(8.dp)
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistScreenPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            PlaylistScreen(
                playlistId = 1L,
                onBackClick = {},
                onTrackClick = {}
            )
        }
    }
}
