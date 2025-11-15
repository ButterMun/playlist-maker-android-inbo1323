package com.practicum.playlistmaker.creator

import com.practicum.playlistmaker.data.network.TrackDto

class Storage {
    private val listTracks = listOf(
        TrackDto("Bohemian Rhapsody", "Queen", 354000),
        TrackDto("We Will Rock You", "Queen", 122000),
        TrackDto("Another One Bites the Dust", "Queen", 215000),
        TrackDto("Like a Rolling Stone", "Bob Dylan", 369000),
        TrackDto("Blowin' in the Wind", "Bob Dylan", 165000),
        TrackDto("Knockin' on Heaven's Door", "Bob Dylan", 145000),
        TrackDto("Smells Like Teen Spirit", "Nirvana", 301000),
        TrackDto("Come As You Are", "Nirvana", 219000),
        TrackDto("Heart-Shaped Box", "Nirvana", 286000),
        TrackDto("Imagine", "John Lennon", 183000),
        TrackDto("Instant Karma!", "John Lennon", 184000),
        TrackDto("Happy Xmas (War Is Over)", "John Lennon", 211000),
        TrackDto("What's Going On", "Marvin Gaye", 232000),
        TrackDto("Sexual Healing", "Marvin Gaye", 238000),
        TrackDto("Ain't No Mountain High Enough", "Marvin Gaye", 148000)
    )

    fun search(request: String): List<TrackDto> {
        return listTracks.filter {
            it.trackName.lowercase().contains(request.lowercase()) ||
                    it.artistName.lowercase().contains(request.lowercase())
        }
    }
}
