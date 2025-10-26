package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.creator.Storage
import com.practicum.playlistmaker.domain.network.NetworkClient

class RetrofitNetworkClient(private val storage: Storage) : NetworkClient {
    override fun doRequest(request: Any): TracksSearchResponse {
        val searchList = storage.search((request as TracksSearchRequest).expression)
        return TracksSearchResponse(searchList).apply { resultCode = 200 }
    }
}