package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.domain.network.NetworkClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val api: ItunesApiService

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"
        private const val DEFAULT_LIMIT = 200
    }

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ItunesApiService::class.java)
    }

    override fun doRequest(dto: Any): TracksSearchResponse {
        val request = dto as? TracksSearchRequest
            ?: return TracksSearchResponse(emptyList()).apply { resultCode = 400 }

        val call: Call<ItunesSearchResponseDto> = api.search(term = request.expression, limit = DEFAULT_LIMIT)

        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                val body = response.body()
                val mapped = (body?.results ?: emptyList()).mapNotNull { itunesToDto(it) }
                TracksSearchResponse(mapped).apply { resultCode = response.code() }
            } else {
                TracksSearchResponse(emptyList()).apply { resultCode = response.code() }
            }
        } catch (_: IOException) {
            // Сетевая ошибка / таймаут
            TracksSearchResponse(emptyList()).apply { resultCode = 500 }
        } catch (_: Throwable) {
            TracksSearchResponse(emptyList()).apply { resultCode = 500 }
        }
    }


    private fun itunesToDto(it: ItunesTrackDto): TrackDto? {
        // Если нет ни имени трека, ни артиста - пропускаем
        val name = it.trackName?.trim()
        val artist = it.artistName?.trim()
        if (name.isNullOrEmpty() && artist.isNullOrEmpty()) return null

        val millis = it.trackTimeMillis?.toInt() ?: 0
        return TrackDto(
            trackName = name ?: "",
            artistName = artist ?: "",
            trackTimeMillis = millis,
            artworkUrl100 = it.artworkUrl100 ?: ""
        )
    }
}

