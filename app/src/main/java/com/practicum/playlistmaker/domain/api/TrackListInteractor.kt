package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track

interface TrackListInteractor {
    fun search(expression: String, consumer: TrackListConsumer)

    interface TrackListConsumer {
        fun consume(foundTracks: Resource<List<Track>>)
    }
}