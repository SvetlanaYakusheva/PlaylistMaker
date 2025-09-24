package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun getSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
       consumer.consume(repository.getSearchHistory())
    }

    override fun clearHistory() {
       repository.clearHistory()
    }

    override fun addTrackToSearchHistory(track: Track) {
       repository.addTrackToSearchHistory(track)
    }
}