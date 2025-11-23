package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.concurrent.CopyOnWriteArrayList

class DatabaseMock(
) {
    private val playlistsList = CopyOnWriteArrayList<Playlist>()
    private val tracksList = CopyOnWriteArrayList<Track>()

    private val playlistsFlow = MutableStateFlow(playlistsList.toList())
    private val tracksFlow = MutableStateFlow(tracksList.toList())

    // История поиска
    private val historyList = mutableListOf<String>()
    private val historyFlow = MutableStateFlow(historyList.toList())

    fun getHistory(): Flow<List<String>> = historyFlow

    fun addToHistory(word: String) {
        if (word.isNotBlank() && !historyList.contains(word)) {
            historyList.add(0, word) // добавляем в начало
            if (historyList.size > 10) historyList.removeAt(historyList.lastIndex) // ограничим историю 10
            historyFlow.value = historyList.toList()
        }
    }

    // Плейлисты
    fun getAllPlaylists(): Flow<List<Playlist>> =
        combine(playlistsFlow, tracksFlow) { pls, trs ->
            pls.map { p -> p.copy(tracks = trs.filter { it.playlistId == p.id }) }
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
        playlistsList.removeIf { it.id == id }
        tracksList.replaceAll { t -> if (t.playlistId == id) t.copy(playlistId = 0L) else t }
        playlistsFlow.value = playlistsList.toList()
        tracksFlow.value = tracksList.toList()
    }

    // Трэки
    fun getTrackByNameAndArtist(trackName: String, artistName: String): Flow<Track?> =
        tracksFlow.map { list -> list.find { it.trackName == trackName && it.artistName == artistName } }

    fun insertTrack(track: Track) {
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

