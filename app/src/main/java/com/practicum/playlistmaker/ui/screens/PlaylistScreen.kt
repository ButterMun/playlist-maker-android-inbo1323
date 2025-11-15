package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlist: Playlist,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Верх
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.white)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )
            }

            // Информация о треках
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.white)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(AppColors.lightGray, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎵", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Название плейлиста
                Text(
                    text = playlist.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.black
                )

                // Описание плейлиста
                if (playlist.description.isNotEmpty()) {
                    Text(
                        text = playlist.description,
                        fontSize = 14.sp,
                        color = AppColors.gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Статистика
                val totalTracks = playlist.tracks.size
                val totalMinutes = playlist.tracks.sumOf { track ->
                    val parts = track.trackTime.split(":")
                    if (parts.size == 2) parts[0].toIntOrNull() ?: 0 else 0
                }

                Text(
                    text = stringResource(
                        R.string.playlist_tracks_info,
                        totalTracks,
                        totalMinutes
                    ),
                    fontSize = 12.sp,
                    color = AppColors.gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Список треков
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(playlist.tracks) { track ->
                    TrackRow(track)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                }
            }
        }
    }
}

@Composable
fun TrackRow(track: Track) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = null,
            tint = AppColors.gray,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = track.trackName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.black
            )
            Text(
                text = "${track.artistName} • ${track.trackTime}",
                fontSize = 12.sp,
                color = AppColors.gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistScreenPreview() {
    val sampleTracks = listOf(
        Track("Bohemian Rhapsody", "Queen", "05:55"),
        Track("Stairway to Heaven", "Led Zeppelin", "08:03"),
        Track("Hotel California", "Eagles", "06:30")
    )
    val samplePlaylist = Playlist(
        id = 1L,
        name = "Рок-хиты",
        description = "Лучшие рок-композиции всех времен",
        tracks = sampleTracks
    )

    PlaylistMakerTheme {
        PlaylistScreen(
            playlist = samplePlaylist,
            onBackClick = {}
        )
    }
}
