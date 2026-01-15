package com.practicum.playlistmaker.library.playlists.data.impl

import androidx.room.withTransaction
import com.practicum.playlistmaker.library.favorites.data.TrackDbConvertor
import com.practicum.playlistmaker.library.favorites.data.db.FavoritesDatabase
import com.practicum.playlistmaker.library.playlists.data.PlaylistDbConverter
import com.practicum.playlistmaker.library.playlists.data.db.PlaylistDatabase
import com.practicum.playlistmaker.library.playlists.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.playlists.data.db.SummaryPlaylistDatabase
import com.practicum.playlistmaker.library.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDatabase: PlaylistDatabase,
    private val playlistDbConvertor: PlaylistDbConverter,
    private val summaryPlaylistDatabase: SummaryPlaylistDatabase,
    private val trackDbConvertor: TrackDbConvertor,
    private val favoritesDatabase: FavoritesDatabase
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
            .addTrackToPlaylist(playlistDbConvertor.map(listOf(track.trackId)+playlist.listIds),
                                                        playlist.listIds.size + 1,
                                                        playlist.id)
    }

    private fun convertFromPlaylistEntity(playlistsList: List<PlaylistEntity>): List<Playlist> {
        return playlistsList.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    override suspend fun deleteAllPlaylists() {
        playlistDatabase.playlistDao().deleteAllPlaylists()
        summaryPlaylistDatabase.summaryPlaylistDao().deleteAllTracksFromSummaryPlaylist()
    }

    override suspend fun addTrackToSummaryPlaylist(track: Track) {
        summaryPlaylistDatabase.summaryPlaylistDao()
            .insertTrackToSummaryPlaylist(trackDbConvertor.map(track))
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist {
        return playlistDbConvertor.map(
                            playlistDatabase.playlistDao().getPlaylistById(playlistId))
    }

     override fun getPlaylistByIdFlow(playlistId: Int): Flow<Playlist> {

         return playlistDatabase.playlistDao().getPlaylistByIdLive(playlistId).map { entity ->
             playlistDbConvertor.map(entity)
         }
             .distinctUntilChanged()
     }

     override fun getTracksFromPlaylist(ids: List<Int>): Flow<List<Track>> = flow {
         val listIdsFavorites = favoritesDatabase.favoritesDao().getFavoritesIds()
         val summaryPlaylist =trackDbConvertor.mapList(
                                             summaryPlaylistDatabase.summaryPlaylistDao().getAllTracks(),
                                             listIdsFavorites)


         val summaryPlaylistMap = summaryPlaylist.associateBy { it.trackId }

         emit(ids.mapNotNull { summaryPlaylistMap[it] })
     }

     override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {

         val playlist: Playlist = playlistDbConvertor.map(playlistDatabase.playlistDao().getPlaylistById(playlistId))
         val newListIds: List<Int> = playlist.listIds - track.trackId
         playlistDatabase.playlistDao()
             .deleteTrackFromPlaylist(playlistDbConvertor.map(newListIds),
                 newListIds.size,
                 playlistId)

         val idsOfTracks = playlistDatabase.playlistDao().getListIdsFromAllPlaylists().map {
             listIds -> playlistDbConvertor.mapFromJsonToList(listIds)
         }

         for (playlistTracksId in idsOfTracks){
             if (track.trackId in playlistTracksId) return
         }

         summaryPlaylistDatabase.summaryPlaylistDao().deleteTrackFromSummaryPlaylist(track.trackId)

     }

    override suspend fun deletePlaylistById(playlistId: Int) {

        playlistDatabase.withTransaction {

            val playlistEntity = playlistDatabase.playlistDao().getPlaylistById(playlistId)
            val tracksInPlaylist = playlistDbConvertor.map(playlistEntity).listIds

            playlistDatabase.playlistDao().deletePlaylistById(playlistId)
            for (id in tracksInPlaylist) {
                deleteTrackFromSummaryPlaylist(id)
            }
        }
    }

    override suspend fun updatePlaylist(
        id: Int,
        newName: String,
        newDescription: String?,
        newCoverImageUri: String?
    ) {
        playlistDatabase.playlistDao().updatePlaylist(id, newName, newDescription, newCoverImageUri)
    }

    private suspend fun deleteTrackFromSummaryPlaylist(trackId: Int) {
        val idsOfTracks = playlistDatabase.playlistDao().getListIdsFromAllPlaylists().map {
                listIds -> playlistDbConvertor.mapFromJsonToList(listIds)
        }

        for (playlistTracksId in idsOfTracks){
            if (trackId in playlistTracksId) return
        }

        summaryPlaylistDatabase.summaryPlaylistDao().deleteTrackFromSummaryPlaylist(trackId)
    }
}
