package com.practicum.playlistmaker.library.playlists.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import java.io.Serializable


class PlaylistAdapter () : RecyclerView.Adapter<PlaylistViewHolder> (),
    Serializable {
    var playlistList: MutableList<Playlist> = mutableListOf()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlistList[position])

        holder.itemView.setOnClickListener {}
    }
    override fun getItemCount(): Int {
        return playlistList.size
    }


}