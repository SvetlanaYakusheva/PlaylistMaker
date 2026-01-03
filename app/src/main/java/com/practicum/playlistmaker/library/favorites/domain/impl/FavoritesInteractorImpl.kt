package com.practicum.playlistmaker.library.favorites.domain.impl

import com.practicum.playlistmaker.library.favorites.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.favorites.domain.FavoritesRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {
    override suspend fun addFavorite(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun deleteFavorite(track: Track) {
        favoritesRepository.deleteFromFavorites(track)
    }

    override fun getFavoritesList(): Flow<List<Track>> {
        return favoritesRepository.getFavoritesList()
    }

}

