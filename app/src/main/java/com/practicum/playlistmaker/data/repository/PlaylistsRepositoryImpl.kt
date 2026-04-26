package com.practicum.playlistmaker.data.repository

import com.practicum.playlistmaker.data.db.AppDatabase
import com.practicum.playlistmaker.data.db.PlaylistEntity
import com.practicum.playlistmaker.domain.models.Playlist
import com.practicum.playlistmaker.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsRepositoryImpl(
    database: AppDatabase
) : PlaylistsRepository {

    private val playlistDao = database.playlistDao()
    private val trackDao = database.trackDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return combine(
            playlistDao.getPlaylist(playlistId),
            trackDao.getTracksByPlaylistId(playlistId)
        ) { playlistEntity, trackEntities ->
            playlistEntity?.toDomain(trackEntities.map { it.toDomain() })
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return combine(
            playlistDao.getAllPlaylists(),
            trackDao.getAllTracks()
        ) { playlistEntities, allTrackEntities ->
            playlistEntities.map { playlistEntity ->
                val playlistTracks = allTrackEntities.filter { it.playlistId == playlistEntity.id }
                playlistEntity.toDomain(playlistTracks.map { it.toDomain() })
            }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String, coverImageUri: String?) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description,
                coverImageUri = coverImageUri
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        trackDao.removeTracksFromPlaylist(id)
        playlistDao.deletePlaylistById(id)
    }
}

private fun PlaylistEntity.toDomain(tracks: List<com.practicum.playlistmaker.domain.models.Track> = emptyList()): Playlist {
    return Playlist(
        id = id,
        name = name,
        description = description,
        coverImageUri = coverImageUri,
        tracks = tracks
    )
}
