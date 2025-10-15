package com.practicum.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityAudioplayerBinding
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.getCoverArtwork
import com.practicum.playlistmaker.util.getDateFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlayerActivity : AppCompatActivity() {

    private var track: Track? = null
    private var url = ""
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(url)
    }

    private lateinit var binding: ActivityAudioplayerBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra(KEY_TRACK_TO_AUDIOPLAYER, Track::class.java)

        binding.trackName.text = track?.trackName
        binding.artistName.text = track?.artistName
        binding.trackTime.text = getDateFormat(track?.trackTime)
        if (track?.collectionName == null) {
            binding.collectionName.visibility = View.GONE
            binding.textviewCollectionName.visibility = View.GONE
        } else {
            binding.collectionName.text = track?.collectionName
        }
        if (track?.releaseDate == null) {
            binding.releaseDate.visibility = View.GONE
            binding.textviewReleaseDate.visibility = View.GONE
        } else {
            binding.releaseDate.text = track?.releaseDate!!.substring(0, 4)
        }
        binding.genre.text = track?.primaryGenreName
        binding.country.text = track?.country

        Glide.with(this)
            .load(getCoverArtwork(track?.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, this)))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(binding.trackCover)

        val buttonBack = findViewById<ImageButton>(R.id.back_button_AudioPlayerActivity)
        buttonBack.setOnClickListener {
            finish()
        }

        url = track?.previewUrl ?: ""

        viewModel.observePlayerState().observe(this) {
            changeButtonImage(it == PlayerViewModel.STATE_PLAYING)
        }

        viewModel.observeProgressTime().observe(this) {
            binding.timer.text = (it)
        }
        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    private fun changeButtonImage(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playButton.setImageResource(R.drawable.pause_button_100)
        } else {
            binding.playButton.setImageResource(R.drawable.play_button_100)
        }
    }
    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    companion object {
        const val KEY_TRACK_TO_AUDIOPLAYER = "KEY_TRACK_TO_AUDIOPLAYER"
    }
}