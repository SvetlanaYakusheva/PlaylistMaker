package com.practicum.playlistmaker.player.ui.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import java.io.Serializable

class PlaylistBottomSheetAdapter (private val clickListener: PlaylistClickListener) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder> (),
    Serializable {
    var playlistList: MutableList<Playlist> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_bottomsheet_view, parent, false)
        return PlaylistBottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        holder.bind(playlistList[position])

        holder.itemView.setOnClickListener {clickListener.onPlaylistClick(playlistList[position])}
    }
    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

}