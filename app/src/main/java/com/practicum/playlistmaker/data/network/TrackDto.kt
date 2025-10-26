package com.practicum.playlistmaker.data.network

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Int
)