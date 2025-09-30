package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.search.domain.model.Track

interface TrackListRepository {
    fun search(expression: String): Resource<List<Track>>
}
