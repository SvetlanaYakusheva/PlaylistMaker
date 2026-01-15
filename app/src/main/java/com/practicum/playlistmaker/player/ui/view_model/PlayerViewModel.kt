package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.favorites.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.playlists.ui.PlaylistsState
import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.getDateFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel (
                        private val track: Track,
                        private val favoritesInteractor: FavoritesInteractor,
                        private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val favoriteState = MutableLiveData<Boolean>(track.isFavorite)
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

    private val playlistsState = MutableLiveData<PlaylistsState>()
    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsState

    private val addToPlaylistState = MutableLiveData<Pair<Boolean?, String>> (Pair(null, ""))
    fun observeAddToPlaylistState(): LiveData<Pair<Boolean?, String>> = addToPlaylistState

    private val mediaPlayer = MediaPlayer()

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    //раздел с работой Плеера
    fun onPause() {
        pausePlayer()
    }

    fun onPlayButtonClicked() {
        when(playerState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)

        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {

            playerState.postValue(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            playerState.postValue(PlayerState.Prepared())
            timerJob?.cancel()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerState.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(PLAYER_DELAY_MILLIS)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }
    private fun getCurrentPlayerPosition(): String {
        return getDateFormat(mediaPlayer.currentPosition.toLong())
    }

    //раздел с добавлением в Избранное
    fun onFavoriteButtonClicked(track: Track) {
        viewModelScope.launch {
            if (!track.isFavorite) {
                favoritesInteractor.addFavorite(track)
            } else {
                favoritesInteractor.deleteFavorite(track)
            }
            track.isFavorite = !track.isFavorite
            favoriteState.postValue(track.isFavorite)
        }
    }
    //раздел с плейлистами
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
            playlistsState.postValue(PlaylistsState.Empty)
        } else {
            playlistsState.postValue(PlaylistsState.Content(playlistsList))
        }
    }


    fun onPlaylistClicked(track: Track, playlist: Playlist) {
        if (track.trackId in playlist.listIds) {
            addToPlaylistState.value = Pair(false, playlist.name)
        } else {
            viewModelScope.launch {
                playlistInteractor
                    .addTrackToPlaylist(track, playlist)
            }

            addToPlaylistState.value = Pair(true, playlist.name)
            //обновляем в BottomSheet отражаемое количество треков в плейлисте после добавления
            fillData()
        }
    }

    companion object {
        private const val PLAYER_DELAY_MILLIS = 300L
    }
}
