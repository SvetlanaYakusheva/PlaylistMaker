package com.practicum.playlistmaker.library.playlists.domain

interface ResourceProvider {
    fun getQuantityString(resId: Int, quantity: Int, vararg args: Any): String

}