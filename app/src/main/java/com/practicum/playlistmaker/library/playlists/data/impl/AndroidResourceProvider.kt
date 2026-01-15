package com.practicum.playlistmaker.library.playlists.data.impl

import android.content.Context
import com.practicum.playlistmaker.library.playlists.domain.ResourceProvider

class AndroidResourceProvider(private val context: Context) : ResourceProvider {
    override fun getQuantityString(resId: Int, quantity: Int, vararg args: Any): String {
        return context.resources.getQuantityString(resId, quantity, *args)
    }



}