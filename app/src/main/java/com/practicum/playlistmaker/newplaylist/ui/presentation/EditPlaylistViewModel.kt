package com.practicum.playlistmaker.newplaylist.ui.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.newplaylist.ui.EditPlaylistState
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistId: Int, private val playlistInteractor: PlaylistInteractor) : NewPlaylistViewModel(playlistInteractor) {

    init {
        fillData()
    }

    private val editPlaylistState = MutableLiveData<EditPlaylistState>()
    fun observeEditPlaylistState(): LiveData<EditPlaylistState> = editPlaylistState

    private fun fillData()  {
         viewModelScope.launch {
             processResult(playlistInteractor.getPlaylistById(playlistId))
        }

    }

    private fun processResult(playlist: Playlist) {
            editPlaylistState.postValue(EditPlaylistState.Content(playlist))
    }

    fun onUpdatePlaylistButtonClicked(id: Int, name: String, description: String, uri: Uri?) {
        viewModelScope.launch {
            playlistInteractor
                .updatePlaylist( id, name, description, uri.toString())
        }
    }
}