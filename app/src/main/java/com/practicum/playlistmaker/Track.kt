package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") var trackTime: Long, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
)