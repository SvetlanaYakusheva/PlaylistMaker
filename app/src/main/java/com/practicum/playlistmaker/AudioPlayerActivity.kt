package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class AudioPlayerActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var play: ImageButton
    private lateinit var timer: TextView
    private var track: Track? = null
    private var mediaPlayer = MediaPlayer()
    private var timerSeconds = 0

    private var playerState = STATE_DEFAULT

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        track = intent.getParcelableExtra(KEY_TRACK_TO_AUDIOPLAYER, Track::class.java)

        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)
        val trackTime: TextView = findViewById(R.id.track_time)
        val artWork: ImageView = findViewById(R.id.trackCover)
        val album: TextView = findViewById(R.id.collectionName)
        val year: TextView = findViewById(R.id.releaseDate)
        val genre: TextView = findViewById(R.id.genre)
        val country: TextView = findViewById(R.id.country)

        trackName.text = track?.trackName
        artistName.text = track?.artistName
        trackTime.text = getDateFormat(track?.trackTime)
        if (track?.collectionName == null) {
            album.visibility = View.GONE
            val album_textview: TextView = findViewById(R.id.textview_collectionName)
            album_textview.visibility = View.GONE
        } else {
            album.text = track?.collectionName
        }
        if (track?.releaseDate == null) {
            year.visibility = View.GONE
            val year_textview: TextView = findViewById(R.id.textview_releaseDate)
            year_textview.visibility = View.GONE
        } else {
            year.text = track?.releaseDate!!.substring(0, 4)
        }
        genre.text = track?.primaryGenreName
        country.text = track?.country

        Glide.with(this)
            .load(getCoverArtwork(track?.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, this)))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(artWork)

        val buttonBack = findViewById<ImageButton>(R.id.back_button_AudioPlayerActivity)
        buttonBack.setOnClickListener {
            finish()
        }

        play = findViewById(R.id.play_button)
        preparePlayer()

        play.setOnClickListener {
            playbackControl()

        }
        timer= findViewById(R.id.timer)


    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track?.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {

            playerState = STATE_PREPARED
            play.setImageResource(R.drawable.play_button_100)

            handler.removeCallbacksAndMessages(null)
            timer.text = getString(R.string.time_of_song)
            timerSeconds = 0

        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        play.setImageResource(R.drawable.pause_button_100)

        handler.postDelayed(runnable, PLAYER_DELAY_MILLIS)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        play.setImageResource(R.drawable.play_button_100)

        handler.removeCallbacksAndMessages(null)
        timerSeconds = mediaPlayer.currentPosition / 1000
        handler.post {
            timer.text = String.format("%d:%02d", timerSeconds / 60, timerSeconds % 60)
        }
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    private val runnable = object : Runnable {
        override fun run() {
            timerSeconds = mediaPlayer.currentPosition / 1000
            timer.text =  String.format("%d:%02d", timerSeconds / 60, timerSeconds % 60)
            handler.postDelayed(this, PLAYER_DELAY_MILLIS)
        }
    }
    companion object {
        const val KEY_TRACK_TO_AUDIOPLAYER = "KEY_TRACK_TO_AUDIOPLAYER"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val PLAYER_DELAY_MILLIS = 300L
    }
}