package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TrackListRequest
import com.practicum.playlistmaker.data.dto.TrackListResponse
import com.practicum.playlistmaker.domain.api.TrackListRepository
import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track

class TrackListRepositoryImpl (private val networkClient: NetworkClient) : TrackListRepository {

    override fun search(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackListRequest(expression))
        //сюда приходит правильный ответ на запрос пока что - список треков по запросу
        if (response.resultCode == 200) {
            val list = (response as TrackListResponse).results.map {
                Track(
                        it.trackId,
                        it.trackName, // Название композиции
                        it.artistName, // Имя исполнителя
                        it.trackTime, // Продолжительность трека
                        it.artworkUrl100, // Ссылка на изображение обложки
                        it.collectionName, //Название альбома
                        it.releaseDate, // год релиза
                        it.primaryGenreName, // название жанра
                        it.country, // страна исполнителя
                        it.previewUrl
                )}
            return Resource.Success(list)
        } else {
            return Resource.Error("Произошла сетевая ошибка")
        }
    }
} 