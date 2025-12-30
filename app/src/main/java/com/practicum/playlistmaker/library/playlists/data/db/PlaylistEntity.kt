package com.practicum.playlistmaker.library.playlists.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)  val id: Int = 0,
    val name: String,
    val description: String?,
    val coverImageUri: String?,
    val listIds: String,
    val playlistSize: Int,
    val timeOfAddingToFavorites: String // //время добавления в Избранное для правильной сортировки от нового к старому
)

