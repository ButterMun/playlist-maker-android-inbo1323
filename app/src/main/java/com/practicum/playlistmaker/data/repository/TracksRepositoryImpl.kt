package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.network.TracksSearchRequest
import com.practicum.playlistmaker.data.network.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.network.NetworkClient
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    val database: DatabaseMock
) : TracksRepository {

    override suspend fun searchTracks(expression: String): List<Track> {
        return withContext(Dispatchers.IO) {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            if (response.resultCode == 200) {
                (response as TracksSearchResponse).results.map { dto ->
                    val seconds = dto.trackTimeMillis / 1000
                    val minutes = seconds / 60
                    val trackTime = "%02d:%02d".format(minutes, seconds - minutes * 60)
                    Track(
                        trackName = dto.trackName,
                        artistName = dto.artistName,
                        trackTime = trackTime,
                        artworkUrl100 = dto.artworkUrl100
                    )
                }
            } else {
                throw IOException("Ошибка сети: ${response.resultCode}") as Throwable
            }
        }
    }


    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track.trackName, track.artistName)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    override suspend fun insertSongToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track.copy(playlistId = playlistId))
    }

    override suspend fun deleteSongFromPlaylist(track: Track) {
        database.insertTrack(track.copy(playlistId = 0L))
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(favorite = isFavorite))
    }

    override suspend fun saveTrack(track: Track) {
        database.insertTrack(track)
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }


}
