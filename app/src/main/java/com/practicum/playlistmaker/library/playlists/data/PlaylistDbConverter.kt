package com.practicum.playlistmaker.library.playlists.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.library.playlists.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import java.time.LocalDateTime

class PlaylistDbConverter (private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            0,
            playlist.name, // Название композиции
            playlist.description, // Имя исполнителя
            playlist.coverImageUri, // Продолжительность трека
            gson.toJson(playlist.listIds),
            playlist.listIds.size,
            LocalDateTime.now().toString()
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.name, // Название композиции
            playlist.description, // Имя исполнителя
            playlist.coverImageUri, // Продолжительность трека
            gson.fromJson(playlist.listIds, object : TypeToken<List<Int>>() {}.type),
            playlist.playlistSize)

    }
    fun map (listIds:  List<Int>) : String {
        return gson.toJson(listIds)
    }
}
