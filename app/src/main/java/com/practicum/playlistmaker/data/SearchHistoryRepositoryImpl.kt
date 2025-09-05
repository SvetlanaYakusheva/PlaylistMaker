package com.practicum.playlistmaker.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl (private val sharedPreferences: SharedPreferences) : SearchHistoryRepository {
    private var trackList: MutableList<Track> = mutableListOf()

    override fun getSearchHistory(): MutableList<Track> {
        trackList.clear()
        trackList.addAll(readFromSharedPreferences())
        return trackList
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY_TRACKLIST)
            .apply()

        trackList.clear()
    }

    override fun addTrackToSearchHistory(track: Track) {
        trackList.clear()
        trackList.addAll(readFromSharedPreferences())
        trackList.remove(track)
        trackList.add(0,track)
        if (trackList.size > SEARCH_HISTORY_SIZE) {
            trackList.removeAt(10)
        }
        writeToSharedPreferences()
    }

    private fun readFromSharedPreferences() : List<Track> {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY_TRACKLIST, null) ?: return emptyList()
        return Gson().fromJson(json, Array<TrackDto>::class.java).map {
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

    }

    private fun writeToSharedPreferences() {
        val list = trackList.map {
            TrackDto(
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
        val json = Gson().toJson(list)
        sharedPreferences.edit()
            .putString(KEY_SEARCH_HISTORY_TRACKLIST, json)
            .apply()
    }
    companion object {
        const val KEY_SEARCH_HISTORY_TRACKLIST = "KEY_SEARCH_HISTORY_TRACKLIST"
        private const val SEARCH_HISTORY_SIZE = 10
    }
}