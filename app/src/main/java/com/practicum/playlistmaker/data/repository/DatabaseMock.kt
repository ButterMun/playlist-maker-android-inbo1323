package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.concurrent.CopyOnWriteArrayList

class DatabaseMock(
    private val scope: CoroutineScope,
) {
    private val playlistsList = CopyOnWriteArrayList<Playlist>()
    private val tracksList = CopyOnWriteArrayList<Track>()

    // StateFlows
    private val playlistsFlow = MutableStateFlow<List<Playlist>>(playlistsList.toList())
    private val tracksFlow = MutableStateFlow<List<Track>>(tracksList.toList())

    // История поиска
    private val historyList = mutableListOf<String>()

    fun getHistory(): List<String> = historyList.toList()

    fun addToHistory(word: String) {
        historyList.add(word)
    }

    // --- Playlists ---

    fun getAllPlaylists(): Flow<List<Playlist>> =
        combine(playlistsFlow, tracksFlow) { pls, trs ->
            pls.map { p ->
                p.copy(tracks = trs.filter { it.playlistId == p.id })
            }
        }

    fun getPlaylist(playlistId: Long): Flow<Playlist?> =
        combine(playlistsFlow, tracksFlow) { pls, trs ->
            val p = pls.find { it.id == playlistId } ?: return@combine null
            p.copy(tracks = trs.filter { it.playlistId == playlistId })
        }

    fun insertPlaylist(playlist: Playlist) {
        val newId = if (playlistsList.isEmpty()) 1L else (playlistsList.maxOf { it.id } + 1L)
        playlistsList.add(playlist.copy(id = newId))
        playlistsFlow.value = playlistsList.toList()
    }

    fun deletePlaylistById(id: Long) {
        // Удаляем плейлист
        playlistsList.removeIf { it.id == id }

        // Сбрасываем playlistId у треков, которые были в этом плейлисте
        tracksList.replaceAll { t -> if (t.playlistId == id) t.copy(playlistId = 0L) else t }

        // Обновляем Flow
        playlistsFlow.value = playlistsList.toList()
        tracksFlow.value = tracksList.toList()
    }

    // --- Tracks ---
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Flow<Track?> =
        tracksFlow.map { list -> list.find { it.trackName == trackName && it.artistName == artistName } }

    fun insertTrack(track: Track) {
        // Удаляем старую версию если есть (одинаковые name+artist)
        tracksList.removeIf { it.trackName == track.trackName && it.artistName == track.artistName }
        tracksList.add(track)
        tracksFlow.value = tracksList.toList()
    }

    fun getFavoriteTracks(): Flow<List<Track>> =
        tracksFlow.map { list -> list.filter { it.favorite } }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracksList.replaceAll { t -> if (t.playlistId == playlistId) t.copy(playlistId = 0L) else t }
        tracksFlow.value = tracksList.toList()
    }
}
