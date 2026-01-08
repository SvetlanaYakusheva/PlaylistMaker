package com.practicum.playlistmaker.library.playlists.domain

import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun savePlaylist(playlist: Playlist)

    fun getPlaylistsList(): Flow<List<Playlist>>


    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun deleteAllPlaylists()

    suspend fun addTrackToSummaryPlaylist(track: Track )

    suspend fun getPlaylistById(playlistId: Int) : Playlist

    fun getPlaylistByIdFlow(playlistId: Int): Flow<Playlist>

    fun getTracksFromPlaylist(ids: List<Int>) : Flow<List<Track>>

    suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int)

    suspend fun deletePlaylistById(playlistId: Int)

    suspend fun updatePlaylist(id: Int, newName: String, newDescription: String?, newCoverImageUri: String?)

}