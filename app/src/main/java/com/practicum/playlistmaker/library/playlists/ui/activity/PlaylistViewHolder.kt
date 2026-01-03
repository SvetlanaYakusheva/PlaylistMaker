package com.practicum.playlistmaker.library.playlists.ui.activity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.util.determingEndOfWord
import com.practicum.playlistmaker.util.dpToPx

class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val playlistCover: ImageView = itemView.findViewById(R.id.playlistCover)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistName)
    private val playlistSize: TextView = itemView.findViewById(R.id.playlistSize)

    fun bind(model: Playlist) {

        playlistName.text = model.name
        playlistSize.text = "${model.playlistSize.toString()} трек${determingEndOfWord(model.playlistSize)}"


        Glide.with(itemView.context)
            .load(model.coverImageUri)
            .transform(CenterCrop(), RoundedCorners(dpToPx(8f, itemView.context)))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(playlistCover)
    }
}


