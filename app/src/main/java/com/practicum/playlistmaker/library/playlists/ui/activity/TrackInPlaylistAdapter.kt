package com.practicum.playlistmaker.library.playlists.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.activity.TrackViewHolder
import java.io.Serializable

class TrackInPlaylistAdapter (private val clickListener: TrackClickListener,
                              private val longClickListener: TrackClickListener): RecyclerView.Adapter<TrackViewHolder> (),
    Serializable {
    var trackList: MutableList<Track> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])

        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(trackList[position])
        }

        holder.itemView.setOnLongClickListener{
            longClickListener.onTrackClick(trackList[position])
            true
        }
    }
    override fun getItemCount(): Int {
        return trackList.size
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}