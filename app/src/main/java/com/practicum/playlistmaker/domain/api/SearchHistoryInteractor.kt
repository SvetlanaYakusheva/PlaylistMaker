package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

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
