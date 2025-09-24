package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.model.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val foundTracks: List<Track>
    ) : SearchState

    data class ContentHistory(
        val foundSearchHistoryTracks: List<Track>
    ) : SearchState

//    data class Error(
//        val errorMessage: String
//    ) : SearchState
//
//    data class Empty(
//        val message: String
//    ) : SearchState

    object Error : SearchState

    object Empty : SearchState

    object ClearActivity : SearchState

}