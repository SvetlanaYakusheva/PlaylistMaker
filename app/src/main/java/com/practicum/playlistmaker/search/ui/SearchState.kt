package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    data object Loading : SearchState

    data class Content(
        val foundTracks: List<Track>
    ) : SearchState

    data class ContentHistory(
        val foundSearchHistoryTracks: List<Track>
    ) : SearchState

    data object Error : SearchState

    data object Empty : SearchState

    data object ClearActivity : SearchState

}