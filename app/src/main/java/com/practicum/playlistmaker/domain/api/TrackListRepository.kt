package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track

interface TrackListRepository {
    fun search(expression: String): Resource<List<Track>>
}
