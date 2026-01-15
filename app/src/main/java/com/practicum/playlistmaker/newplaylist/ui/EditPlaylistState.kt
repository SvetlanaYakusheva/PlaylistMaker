package com.practicum.playlistmaker.newplaylist.ui

import com.practicum.playlistmaker.library.playlists.domain.model.Playlist

sealed interface EditPlaylistState {

    data class Content(
        val playlist: Playlist,

    ) : EditPlaylistState

    data object Empty : EditPlaylistState

}