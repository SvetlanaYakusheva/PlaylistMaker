package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackListRequest
import com.practicum.playlistmaker.search.data.dto.TrackListResponse
import com.practicum.playlistmaker.search.domain.api.TrackListRepository
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackListRepositoryImpl (private val networkClient: NetworkClient) : TrackListRepository {

    override fun search(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackListRequest(expression))

        when (response.resultCode) {
            200 -> {
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
                    )
                }
                emit(Resource.Success(list))
            }
            404 -> {
                emit(Resource.Success(emptyList()))
            }
            else -> {
                emit(Resource.Error("Произошла сетевая ошибка"))
            }
        }
    }
} 