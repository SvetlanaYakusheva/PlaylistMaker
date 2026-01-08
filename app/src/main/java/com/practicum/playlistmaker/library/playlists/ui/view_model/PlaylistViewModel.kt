package com.practicum.playlistmaker.library.playlists.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.ui.PlaylistState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.determingEndOfWord
import com.practicum.playlistmaker.util.getDateFormat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel (
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()
    val job = viewModelScope.launch {
                                 playlistInteractor.getPlaylistByIdFlow(playlistId)
                                     .collect { playlist ->
                                         currentPlaylist = playlist
                                         playlistInteractor
                                             .getTracksFromPlaylist(playlist)
                                             .collect { tracks ->
                                                 currentTracks = tracks
                                                 processResult(playlist, tracks)
                                             }
                                     }
    }

    private val playlistState = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

    private fun processResult(playlist: Playlist, tracks: List<Track>) {
        if (tracks.isEmpty()) {
            playlistState.postValue(PlaylistState.Empty(playlist))
        } else {
            var duration: Long = 0
            for (track in tracks) {
                duration += track.trackTime
            }
            playlistState.postValue(PlaylistState.Content(
                playlist,
                tracks,
                SimpleDateFormat("mm", Locale.getDefault()).format(duration)))
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
           playlistInteractor.deleteTrackFromPlaylist(track, playlistId)
        }

    }

    fun onShareButtonClickEvent() {
        var playlistToShare = "${currentPlaylist?.name} " +
                "\n${currentPlaylist?.description} " +
                "\n${currentPlaylist?.playlistSize} трек${determingEndOfWord(currentPlaylist!!.playlistSize, "трек")}"
        var number = 1
        for (track in currentTracks) {
            playlistToShare += "\n$number. ${track.artistName} - ${track.trackName} (${getDateFormat(track.trackTime)})"
            ++number
        }
        playlistInteractor.sharePlaylist(playlistToShare)
    }

    fun onDeleteButtonClickEvent() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistById(playlistId)
        }
    }
}

