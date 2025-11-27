package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.TrackEntity
import com.practicum.playlistmaker.data.network.TracksSearchRequest
import com.practicum.playlistmaker.data.network.TracksSearchResponse
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.network.NetworkClient
import com.practicum.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    database: AppDatabase
) : TracksRepository {

    private val trackDao = database.trackDao()

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
        return trackDao.getTrackByNameAndArtist(track.trackName, track.artistName)
            .map { entity -> entity?.toDomain() }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return trackDao.getFavoriteTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSongToPlaylist(track: Track, playlistId: Long) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(
                TrackEntity(
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTime = track.trackTime,
                    artworkUrl100 = track.artworkUrl100,
                    favorite = track.favorite,
                    playlistId = playlistId
                )
            )
        }
    }

    override suspend fun deleteSongFromPlaylist(track: Track) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(
                TrackEntity(
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTime = track.trackTime,
                    artworkUrl100 = track.artworkUrl100,
                    favorite = track.favorite,
                    playlistId = 0L
                )
            )
        }
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            trackDao.updateFavoriteStatus(track.trackName, track.artistName, isFavorite)
        }
    }

    override suspend fun saveTrack(track: Track) {
        withContext(Dispatchers.IO) {
            trackDao.insertTrack(
                TrackEntity(
                    trackName = track.trackName,
                    artistName = track.artistName,
                    trackTime = track.trackTime,
                    artworkUrl100 = track.artworkUrl100,
                    favorite = track.favorite,
                    playlistId = track.playlistId
                )
            )
        }
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        withContext(Dispatchers.IO) {
            trackDao.removeTracksFromPlaylist(playlistId)
        }
    }

    override suspend fun cleanupUnusedTracks() {
        withContext(Dispatchers.IO) {
            trackDao.cleanupUnusedTracks()
        }
    }
}

fun TrackEntity.toDomain(): Track {
    return Track(
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        artworkUrl100 = artworkUrl100,
        favorite = favorite,
        playlistId = playlistId
    )
}