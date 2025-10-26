package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.network.TracksSearchRequest
import com.practicum.playlistmaker.data.network.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.network.NetworkClient
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.delay

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000) // Эмуляция задержки
        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map { dto ->
                val seconds = dto.trackTimeMillis / 1000
                val minutes = seconds / 60
                val trackTime = "%02d:%02d".format(minutes, seconds - minutes * 60)
                Track(dto.trackName, dto.artistName, trackTime)
            }
        } else {
            emptyList()
        }
    }
}