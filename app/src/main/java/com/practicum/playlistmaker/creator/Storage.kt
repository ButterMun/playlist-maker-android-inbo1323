package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.network.TrackDto

class Storage {
    private val listTracks = listOf(
        TrackDto("Владивосток 2000", "Мумий Троль", 158000),
        TrackDto("Группа крови", "Кино", 283000),
        // ... остальные треки
    )

    fun search(request: String): List<TrackDto> {
        return listTracks.filter {
            it.trackName.lowercase().contains(request.lowercase())
        }
    }
}