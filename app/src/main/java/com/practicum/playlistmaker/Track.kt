package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") var trackTime: String, // Продолжительность трека
    //var trackTime: String, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
)