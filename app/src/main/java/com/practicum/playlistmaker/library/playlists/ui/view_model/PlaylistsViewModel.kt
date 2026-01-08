package com.practicum.playlistmaker.library.playlists.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.ui.PlaylistsState
import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel (
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    fun fillData() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistsList()
                .collect { playlistsList ->
                    processResult(playlistsList)
                }
        }
    }

    private fun processResult(playlistsList: List<Playlist>) {
        if (playlistsList.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlistsList))
        }
    }

    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }
}