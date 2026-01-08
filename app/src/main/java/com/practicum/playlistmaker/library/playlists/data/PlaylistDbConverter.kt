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
            playlist.name,
            playlist.description,
            playlist.coverImageUri,
            gson.toJson(playlist.listIds),
            playlist.listIds.size,
            LocalDateTime.now().toString()
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverImageUri,
            gson.fromJson(playlist.listIds, object : TypeToken<List<Int>>() {}.type),
            playlist.playlistSize)

    }
    fun map (listIds:  List<Int>) : String {
        return gson.toJson(listIds)
    }

    fun mapFromJsonToList(listIds:  String) :  List<Int> {
        return gson.fromJson(listIds, object : TypeToken<List<Int>>() {}.type)
    }


}
