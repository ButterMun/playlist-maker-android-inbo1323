package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.repository.DatabaseMock
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

object Creator {
    private val appScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO) }
    private val database: DatabaseMock by lazy { DatabaseMock(appScope) }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(Storage()), database)
    }

    fun getPlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(database)
    }
}
