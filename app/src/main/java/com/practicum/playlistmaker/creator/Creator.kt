package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.repository.DatabaseMock
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository

object Creator {
    private val database: DatabaseMock by lazy { DatabaseMock() }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(), database)
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(database)
    }
}
