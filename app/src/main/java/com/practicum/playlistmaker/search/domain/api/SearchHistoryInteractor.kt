package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryInteractor {

    fun getSearchHistory(consumer: SearchHistoryConsumer)
    fun clearHistory(consumer: ClearHistoryConsumer)
    fun addTrackToSearchHistory(track: Track)

    interface SearchHistoryConsumer {
        fun consume(foundSearchHistoryTracks: List<Track>)
    }
    interface ClearHistoryConsumer {
        fun consume(unit: Unit)
    }
}
