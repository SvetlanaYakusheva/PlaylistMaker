package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): MutableList<Track>
    fun clearHistory()
    fun addTrackToSearchHistory(track: Track)


}
