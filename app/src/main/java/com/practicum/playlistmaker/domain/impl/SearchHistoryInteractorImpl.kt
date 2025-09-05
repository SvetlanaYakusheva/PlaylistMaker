package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    override fun getSearchHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
       consumer.consume(repository.getSearchHistory())
    }

    override fun clearHistory(consumer: SearchHistoryInteractor.ClearHistoryConsumer) {
       consumer.consume(repository.clearHistory())
    }

    override fun addTrackToSearchHistory(track: Track) {
       repository.addTrackToSearchHistory(track)
    }
}