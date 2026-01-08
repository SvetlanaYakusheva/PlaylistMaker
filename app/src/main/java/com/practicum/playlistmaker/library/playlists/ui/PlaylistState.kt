package com.practicum.playlistmaker.library.playlists.ui

import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track

sealed interface PlaylistState {

    data class Content(
        val playlist: Playlist,
        val foundtracks: List<Track>,
        val playlistDuration: String
    ) : PlaylistState

    data class Empty(
        val playlist: Playlist
    ): PlaylistState

}