package com.practicum.playlistmaker.library.playlists.ui

import com.practicum.playlistmaker.library.playlists.domain.model.Playlist


sealed interface PlaylistsState {

    data class Content(
        val foundPlaylists: List<Playlist>
    ) : PlaylistsState

    data object Empty : PlaylistsState

}