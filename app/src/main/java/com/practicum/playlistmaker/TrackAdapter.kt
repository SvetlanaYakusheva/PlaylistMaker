package com.practicum.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.Serializable

class TrackAdapter (private val context: Context): RecyclerView.Adapter<TrackViewHolder> (), Serializable {

    var trackList: MutableList<Track> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])


        holder.itemView.setOnClickListener {
            // обработка нажатия на трек - добавление в историю поиска
            val sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            val searchHistory = SearchHistory(sharedPrefs)
            searchHistory.addTrackToSearchHistory(trackList[position])
            // открытие экрана Аудиоплеера при нажатии на трек в списке
            val audioPlayerIntent = Intent(context, AudioPlayerActivity::class.java)
            //audioPlayerIntent.putExtra(AudioPlayerActivity.KEY_TRACK_TO_AUDIOPLAYER_JSON, Gson().toJson(trackList[position]))
            audioPlayerIntent.putExtra(AudioPlayerActivity.KEY_TRACK_TO_AUDIOPLAYER,trackList[position])
            context.startActivity(audioPlayerIntent)
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}