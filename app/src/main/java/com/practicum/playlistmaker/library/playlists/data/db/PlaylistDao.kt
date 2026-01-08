package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface  PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY timeOfAddingToFavorites DESC")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("UPDATE playlist_table SET listIds = :newListIds, playlistSize = :newPlaylistSize WHERE id = :id")
    suspend fun addTrackToPlaylist(newListIds: String, newPlaylistSize: Int, id: Int)

    @Query("DELETE FROM playlist_table")
    suspend fun deleteAllPlaylists()

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    suspend fun getPlaylistById(id: Int): PlaylistEntity

    @Query("UPDATE playlist_table SET listIds = :newListIds, playlistSize = :newPlaylistSize WHERE id = :id")
    suspend fun deleteTrackFromPlaylist(newListIds: String, newPlaylistSize: Int, id: Int)

    @Query("SELECT listIds FROM playlist_table")
    suspend fun getListIdsFromAllPlaylists(): List<String>

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    fun getPlaylistByIdLive(id: Int): Flow<PlaylistEntity>

    @Query("DELETE FROM playlist_table WHERE id = :id")
    suspend fun deletePlaylistById(id: Int)

    @Query("UPDATE playlist_table SET name = :newName, description = :newDescription, coverImageUri = :newCoverImageUri WHERE id = :id")
    suspend fun updatePlaylist(id: Int, newName: String, newDescription: String?, newCoverImageUri: String?)
}