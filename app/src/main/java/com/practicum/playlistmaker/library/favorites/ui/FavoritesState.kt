package com.practicum.playlistmaker.library.favorites.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface FavoritesState {

    data class Content(
        val foundTracks: List<Track>
    ) : FavoritesState

    data object Empty : FavoritesState

}