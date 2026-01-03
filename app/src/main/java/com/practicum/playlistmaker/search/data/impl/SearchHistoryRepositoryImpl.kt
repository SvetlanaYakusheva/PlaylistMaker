package com.practicum.playlistmaker.search.data.impl

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.library.favorites.data.db.FavoritesDatabase
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.runBlocking

class SearchHistoryRepositoryImpl (
                                private val sharedPreferences: SharedPreferences,
                                private val gson: Gson,
                                private val favoritesDatabase: FavoritesDatabase
                                ) : SearchHistoryRepository {
    private var trackList: MutableList<Track> = mutableListOf()

    override fun getSearchHistory(): MutableList<Track> {
        trackList.clear()
        trackList.addAll(readFromSharedPreferences())

        val favoritesIdsList = runBlocking {
            favoritesDatabase.favoritesDao().getFavoritesIds()
        }
        for (track in trackList) {
            track.isFavorite = track.trackId in favoritesIdsList
        }
        return (trackList)

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

        for (trackInTracklist in trackList) {
            if (trackInTracklist.trackId == track.trackId) {
                trackList.remove(trackInTracklist)
                break

            }
        }

        trackList.add(0,track)
        if (trackList.size > SEARCH_HISTORY_SIZE) {
            trackList.removeAt(trackList.lastIndex)
        }
        writeToSharedPreferences()
    }

    private fun readFromSharedPreferences() : List<Track> {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY_TRACKLIST, null) ?: return emptyList()
        return gson.fromJson(json, Array<TrackDto>::class.java).map {
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
            )
        }
        val json = gson.toJson(list)
        sharedPreferences.edit()
            .putString(KEY_SEARCH_HISTORY_TRACKLIST, json)
            .apply()
    }
    companion object {
        const val KEY_SEARCH_HISTORY_TRACKLIST = "KEY_SEARCH_HISTORY_TRACKLIST"
        private const val SEARCH_HISTORY_SIZE = 10

    }
}