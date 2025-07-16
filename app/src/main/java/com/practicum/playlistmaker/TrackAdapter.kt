package com.practicum.playlistmaker

import android.app.Application.MODE_PRIVATE
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

class TrackAdapter (private val context: Context): RecyclerView.Adapter<TrackViewHolder> (), Serializable {

    var trackList: MutableList<Track> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])

        // обработка нажатия на трек - добавление в историю поиска
        holder.itemView.setOnClickListener {
            val sharedPrefs = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
            val searchHistory = SearchHistory(sharedPrefs)
            searchHistory.addTrackToSearchHistory(trackList[position])
        }
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}