package com.practicum.playlistmaker.library.playlists.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist (
    val id: Int,
    val name: String,
    val description: String?,
    val coverImageUri: String?,
    val listIds: List<Int>,
    val playlistSize: Int
): Parcelable