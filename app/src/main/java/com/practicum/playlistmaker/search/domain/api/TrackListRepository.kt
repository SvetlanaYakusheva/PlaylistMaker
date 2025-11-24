package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackListRepository {
    fun search(expression: String): Flow<Resource<List<Track>>>

}
