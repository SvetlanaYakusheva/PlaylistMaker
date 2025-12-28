package com.practicum.playlistmaker.library.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface FavoritesState {

    data class Content(
        val foundTracks: List<Track>
    ) : FavoritesState

    data object Empty : FavoritesState

}