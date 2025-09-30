package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(consumer: SearchHistoryConsumer)
    fun clearHistory()
    fun addTrackToSearchHistory(track: Track)

    fun interface SearchHistoryConsumer {
        fun consume(foundSearchHistoryTracks: List<Track>)
    }

}
