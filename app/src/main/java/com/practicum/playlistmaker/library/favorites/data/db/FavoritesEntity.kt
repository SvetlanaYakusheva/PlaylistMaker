package com.practicum.playlistmaker.library.favorites.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")
data class FavoritesEntity (
    @PrimaryKey
    val trackId: Int,
    val trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    var trackTime: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    var collectionName: String?, //Название альбома
    val releaseDate: String?, // год релиза
    val primaryGenreName: String?, // название жанра
    val country: String, // страна исполнителя
    val previewUrl: String, //30-секундный отрывок
    val timeOfAddingToFavorites: String // //время добавления в Избранное для правильной сортировки от нового к старому
)



