package com.practicum.playlistmaker.library.playlists.domain.impl


import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
        private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.savePlaylist(playlist)
    }

    override fun getPlaylistsList(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylistsList()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistRepository.addTrackToPlaylist(track, playlist)
        playlistRepository.addTrackToSummaryPlaylist(track)
    }

    override suspend fun deleteAllPlaylists() {
        playlistRepository.deleteAllPlaylists()
    }
}