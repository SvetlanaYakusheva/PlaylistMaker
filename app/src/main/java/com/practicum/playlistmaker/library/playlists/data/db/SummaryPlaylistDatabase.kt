package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.favorites.data.db.FavoritesEntity

@Database(version = 1, entities = [FavoritesEntity::class])
abstract class SummaryPlaylistDatabase : RoomDatabase() {

    abstract fun summaryPlaylistDao(): SummaryPlaylistDao

}