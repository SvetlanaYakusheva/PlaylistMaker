package com.practicum.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson

class AudioPlayerActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        // замена на Parcelable
//        val trackInJson = intent.getStringExtra(KEY_TRACK_TO_AUDIOPLAYER_JSON)
//        val track: Track = Gson().fromJson(trackInJson, Track::class.java)

        val track = intent.getParcelableExtra(KEY_TRACK_TO_AUDIOPLAYER, Track::class.java)

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
            album.text = track.collectionName
        }
        if (track?.releaseDate == null) {
            year.visibility = View.GONE
            val year_textview: TextView = findViewById(R.id.textview_releaseDate)
            year_textview.visibility = View.GONE
        } else {
            year.text = track.releaseDate.substring(0, 4)
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


    }

    companion object {
        const val KEY_TRACK_TO_AUDIOPLAYER = "KEY_TRACK_TO_AUDIOPLAYER"

    }
}