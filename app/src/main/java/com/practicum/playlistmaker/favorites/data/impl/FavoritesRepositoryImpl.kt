package com.practicum.playlistmaker.favorites.data.impl

import com.practicum.playlistmaker.favorites.data.TrackDbConvertor
import com.practicum.playlistmaker.favorites.data.db.FavoritesDatabase
import com.practicum.playlistmaker.favorites.data.db.FavoritesEntity
import com.practicum.playlistmaker.favorites.domain.FavoritesRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(
    private val favoritesDatabase: FavoritesDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        favoritesDatabase.favoritesDao().insertFavorite(trackDbConvertor.map(track))
    }

    override suspend fun deleteFromFavorites(track: Track) {
        favoritesDatabase.favoritesDao().deleteFavorite(trackDbConvertor.map(track))
    }

    override fun getFavoritesList(): Flow<List<Track>> = flow {
        val favoritesList = favoritesDatabase.favoritesDao().getFavorites()
        emit(convertFromFavoritesEntity(favoritesList))
    }
    private fun convertFromFavoritesEntity(favoritesList: List<FavoritesEntity>): List<Track> {
        return favoritesList.map { track -> trackDbConvertor.map(track) }
    }
}


