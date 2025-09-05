package com.practicum.playlistmaker.presentation.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.dpToPx
import com.practicum.playlistmaker.presentation.getDateFormat


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artWork: ImageView = itemView.findViewById(R.id.artWork)

    fun bind(model: Track) {

        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = getDateFormat(model.trackTime)

        Glide.with(itemView.context)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(artWork)
    }
}
