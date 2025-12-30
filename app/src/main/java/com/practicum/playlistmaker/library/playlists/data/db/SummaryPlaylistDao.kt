package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.library.favorites.data.db.FavoritesEntity

@Dao
interface SummaryPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToSummaryPlaylist(track: FavoritesEntity)
}