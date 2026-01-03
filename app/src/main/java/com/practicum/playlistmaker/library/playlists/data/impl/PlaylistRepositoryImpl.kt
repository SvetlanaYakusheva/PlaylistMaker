package com.practicum.playlistmaker.library.playlists.data.impl

import com.practicum.playlistmaker.library.favorites.data.TrackDbConvertor
import com.practicum.playlistmaker.library.playlists.data.PlaylistDbConverter
import com.practicum.playlistmaker.library.playlists.data.db.PlaylistDatabase
import com.practicum.playlistmaker.library.playlists.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.playlists.data.db.SummaryPlaylistDatabase
import com.practicum.playlistmaker.library.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistDbConvertor: PlaylistDbConverter,
    private val summaryPlaylistDatabase: SummaryPlaylistDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : PlaylistRepository {
    override suspend fun savePlaylist(playlist: Playlist) {
        playlistDatabase.playlistDao().insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override fun getPlaylistsList(): Flow<List<Playlist>> = flow {
        val playlistsList = playlistDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlistsList))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistDatabase.playlistDao()
            .addTrackToPlaylist(playlistDbConvertor.map(playlist.listIds + listOf(track.trackId)),
                                                        playlist.listIds.size + 1,
                                                        playlist.name)
    }

    private fun convertFromPlaylistEntity(playlistsList: List<PlaylistEntity>): List<Playlist> {
        return playlistsList.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    override suspend fun deleteAllPlaylists() {
        playlistDatabase.playlistDao().deleteAllPlaylists()
    }

    override suspend fun addTrackToSummaryPlaylist(track: Track) {
        summaryPlaylistDatabase.summaryPlaylistDao()
            .insertTrackToSummaryPlaylist(trackDbConvertor.map(track))
    }
}
