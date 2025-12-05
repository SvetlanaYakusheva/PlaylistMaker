package com.practicum.playlistmaker.favorites.data

import com.practicum.playlistmaker.favorites.data.db.FavoritesEntity
import com.practicum.playlistmaker.search.domain.model.Track
import java.time.LocalDateTime

class TrackDbConvertor {

    fun map(track: Track): FavoritesEntity {
        return FavoritesEntity(
                        track.trackId,
                        track.trackName, // Название композиции
                        track.artistName, // Имя исполнителя
                        track.trackTime, // Продолжительность трека
                        track.artworkUrl100, // Ссылка на изображение обложки
                        track.collectionName, //Название альбома
                        track.releaseDate, // год релиза
                        track.primaryGenreName, // название жанра
                        track.country, // страна исполнителя
                        track.previewUrl,
                        LocalDateTime.now().toString()
        )
    }

    fun map(track: FavoritesEntity): Track {
        return Track(
                    track.trackId,
                    track.trackName, // Название композиции
                    track.artistName, // Имя исполнителя
                    track.trackTime, // Продолжительность трека
                    track.artworkUrl100, // Ссылка на изображение обложки
                    track.collectionName, //Название альбома
                    track.releaseDate, // год релиза
                    track.primaryGenreName, // название жанра
                    track.country, // страна исполнителя
                    track.previewUrl,
                    true)
    }
}