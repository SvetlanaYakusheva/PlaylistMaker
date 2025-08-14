package com.practicum.playlistmaker

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    var artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") var trackTime: Long, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    var collectionName: String?, //Название альбома
    val releaseDate: String?, // год релиза
    val primaryGenreName: String, // название жанра
    val country: String // страна исполнителя
) : Parcelable