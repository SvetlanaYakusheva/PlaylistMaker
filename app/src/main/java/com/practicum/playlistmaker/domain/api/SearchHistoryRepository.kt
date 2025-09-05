package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getSearchHistory(): MutableList<Track>
    fun clearHistory()
    fun addTrackToSearchHistory(track: Track)


}
