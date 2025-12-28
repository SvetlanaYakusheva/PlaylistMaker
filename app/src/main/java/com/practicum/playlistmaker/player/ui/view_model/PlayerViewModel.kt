package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorites.domain.FavoritesInteractor
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.getDateFormat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel (
                        private val url: String,
                        isFavorite: Boolean,
                        private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerState

    private val favoriteState = MutableLiveData<Boolean>(isFavorite)
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState

    private val mediaPlayer = MediaPlayer()

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

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
    fun onFavoriteButtonClicked(track: Track) {
        viewModelScope.launch {
            if (!track.isFavorite) {
                favoritesInteractor.addFavorite(track)
            } else {
                favoritesInteractor.deleteFavorite(track)
            }
            favoriteState.postValue(!track.isFavorite)
            track.isFavorite = !track.isFavorite

        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        println(url)
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


    companion object {
        private const val PLAYER_DELAY_MILLIS = 300L
    }
}
