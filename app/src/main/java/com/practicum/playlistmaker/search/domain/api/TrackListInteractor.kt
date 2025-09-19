package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.search.domain.model.Track

interface TrackListInteractor {
    fun search(expression: String, consumer: TrackListConsumer)

    interface TrackListConsumer {
        fun consume(foundTracks: Resource<List<Track>>)
    }
}