package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [PlaylistEntity::class])
abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
}