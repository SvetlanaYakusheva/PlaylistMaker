package com.practicum.playlistmaker.favorites.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(track: FavoritesEntity)

    @Delete
    suspend fun deleteFavorite(track: FavoritesEntity)

    @Query("SELECT * FROM favorites_table ORDER BY timeOfAddingToFavorites DESC")
    suspend fun getFavorites(): List<FavoritesEntity>

    @Query("SELECT trackId FROM favorites_table")
     suspend fun getFavoritesIds(): List<Int>

}