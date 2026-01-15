package com.practicum.playlistmaker.library.playlists.domain.impl


import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigator
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val externalNavigator: ExternalNavigator
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

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistRepository.getPlaylistById(playlistId)
    }

    override suspend fun getPlaylistByIdFlow(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistByIdFlow(playlistId)
    }

    override fun getTracksFromPlaylist(playlist: Playlist): Flow<List<Track>> {
        return playlistRepository.getTracksFromPlaylist(playlist.listIds)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        playlistRepository.deleteTrackFromPlaylist(track, playlistId)
    }

    override suspend fun deletePlaylistById(playlistId: Int) {
        playlistRepository.deletePlaylistById(playlistId)
    }

    override fun sharePlaylist(playlistToShare: String) {
        externalNavigator.sharePlaylist(playlistToShare)
    }

    override suspend fun updatePlaylist(
        id: Int,
        newName: String,
        newDescription: String?,
        newCoverImageUri: String?
    ) {
        playlistRepository.updatePlaylist(id, newName, newDescription, newCoverImageUri)
    }
}