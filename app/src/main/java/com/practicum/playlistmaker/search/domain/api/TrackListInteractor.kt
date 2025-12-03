package com.practicum.playlistmaker.search.domain.api


import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackListInteractor {
    fun search(expression: String): Flow<Pair<List<Track>?, String?>>
}