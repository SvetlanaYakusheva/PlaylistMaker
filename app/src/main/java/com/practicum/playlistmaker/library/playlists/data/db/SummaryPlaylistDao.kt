package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.favorites.data.db.FavoritesEntity

@Dao
interface SummaryPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackToSummaryPlaylist(track: FavoritesEntity)

    @Query("SELECT * FROM favorites_table ORDER BY timeOfAddingToFavorites DESC")
    suspend fun getAllTracks(): List<FavoritesEntity>

    @Query("DELETE FROM favorites_table")
    suspend fun deleteAllTracksFromSummaryPlaylist()

    @Query("DELETE FROM favorites_table WHERE trackId = :trackId")
    suspend fun deleteTrackFromSummaryPlaylist(trackId: Int)
}