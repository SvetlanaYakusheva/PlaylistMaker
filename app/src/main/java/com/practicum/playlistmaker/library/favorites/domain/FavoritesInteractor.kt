package com.practicum.playlistmaker.library.favorites.domain

import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addFavorite(track: Track)
    suspend fun deleteFavorite(track: Track)
    fun getFavoritesList(): Flow<List<Track>>
}