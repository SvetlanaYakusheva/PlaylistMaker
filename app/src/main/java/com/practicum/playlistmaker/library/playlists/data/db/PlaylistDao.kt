package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface  PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY timeOfAddingToFavorites DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("UPDATE playlist_table SET listIds = :newListIds, playlistSize = :newPlaylistSize WHERE name = :playlistName")
    suspend fun addTrackToPlaylist(newListIds: String, newPlaylistSize: Int, playlistName: String)

    @Query("DELETE FROM playlist_table")
    suspend fun deleteAllPlaylists()

}