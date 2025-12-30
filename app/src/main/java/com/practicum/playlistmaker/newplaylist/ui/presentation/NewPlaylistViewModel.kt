package com.practicum.playlistmaker.newplaylist.ui.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel (
            private val playlistInteractor: PlaylistInteractor
): ViewModel() {

    fun onCreateNewPlaylistButtonClicked(name: String, description: String, uri: Uri?) {
        viewModelScope.launch {
            playlistInteractor
                .addPlaylist(Playlist( name, description, uri.toString(), listOf(),0 ))
        }
    }

}
