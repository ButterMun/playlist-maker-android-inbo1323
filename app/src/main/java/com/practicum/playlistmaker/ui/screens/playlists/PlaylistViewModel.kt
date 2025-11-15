package com.practicum.playlistmaker.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()
    val favoriteList = tracksRepository.getFavoriteTracks()

    private val _duplicatePlaylistError = MutableStateFlow(false)
    val duplicatePlaylistError = _duplicatePlaylistError.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.getAllPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
            }
        }
    }

    fun createNewPlaylist(namePlaylist: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allPlaylists = playlistsRepository.getAllPlaylists().first()
            val exists = allPlaylists.any { it.name.equals(namePlaylist, ignoreCase = true) }

            if (!exists) {
                playlistsRepository.addNewPlaylist(namePlaylist, description)
                _duplicatePlaylistError.value = false
            } else {
                _duplicatePlaylistError.value = true
            }
        }
    }

    fun addTrackToPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch {
            tracksRepository.insertSongToPlaylist(track, playlistId)
        }
    }

    fun toggleFavorite(track: Track, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
        }
    }

    fun saveTrackToDatabase(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.saveTrack(track)
        }
    }

    fun deleteSongFromPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteSongFromPlaylist(track)
        }
    }

    fun deletePlaylistById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(id)
            playlistsRepository.deletePlaylistById(id)
        }
    }

    fun isTrackInPlaylist(track: Track) = tracksRepository.getTrackByNameAndArtist(track)

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PlaylistViewModel(
                        Creator.getPlaylistsRepository(),
                        Creator.getTracksRepository()
                    ) as T
                }
            }
    }
}
