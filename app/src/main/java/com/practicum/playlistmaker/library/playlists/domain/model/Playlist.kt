package com.practicum.playlistmaker.library.playlists.domain.model

data class Playlist (

    val name: String,
    val description: String?,
    val coverImageUri: String?,
    val listIds: List<Int>,
    val playlistSize: Int
)