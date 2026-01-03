package com.practicum.playlistmaker.library.favorites.data.db

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(version = 1, entities = [FavoritesEntity::class])
abstract class FavoritesDatabase : RoomDatabase() {

    abstract fun favoritesDao(): FavoritesDao
}