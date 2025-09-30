package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.TrackListInteractor
import com.practicum.playlistmaker.search.domain.api.TrackListRepository
import java.util.concurrent.Executors

class TrackListInteractorImpl (private val repository: TrackListRepository) : TrackListInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TrackListInteractor.TrackListConsumer) {
        if (expression.isNotEmpty()) {
            executor.execute {
                consumer.consume(repository.search(expression))
            }
        }
    }
}