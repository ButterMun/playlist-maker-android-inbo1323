package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository

class PlaylistsRepositoryImpl(
    private val database: DatabaseMock
) : PlaylistsRepository {

    override fun getPlaylist(playlistId: Long) = database.getPlaylist(playlistId)

    override fun getAllPlaylists() = database.getAllPlaylists()

    override suspend fun addNewPlaylist(name: String, description: String) {
        database.insertPlaylist(Playlist(name = name, description = description))
    }

    override suspend fun deletePlaylistById(id: Long) {
        database.deletePlaylistById(id)
    }
}
