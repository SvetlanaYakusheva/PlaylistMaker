package com.practicum.playlistmaker.favorites.domain

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: Track )

    suspend fun deleteFromFavorites(track: Track)

    fun getFavoritesList(): Flow<List<Track>>
}

