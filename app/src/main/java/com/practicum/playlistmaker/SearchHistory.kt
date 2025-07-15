package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory (private val sharedPreferences: SharedPreferences) {
    var trackList: MutableList<Track> = mutableListOf()

    init {
        getSearchHistory()
    }

    fun addTrackToSearchHistory(track: Track) {
        getSearchHistory()
        trackList.remove(track)
        trackList.add(0,track)
        if (trackList.size > SEARCH_HISTORY_SIZE) {
            trackList.removeAt(10)
        }
        writeToSharedPreferences()
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .remove(KEY_SEARCH_HISTORY_TRACKLIST)
            .apply()
        trackList.clear()
    }

    fun getSearchHistory()  {
        trackList.clear()
        trackList.addAll(readFromSharedPreferences())
    }

    private fun readFromSharedPreferences() : Array<Track> {
        val json = sharedPreferences.getString(KEY_SEARCH_HISTORY_TRACKLIST, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    private fun writeToSharedPreferences() {
        val json = Gson().toJson(trackList)
        sharedPreferences.edit()
            .putString(KEY_SEARCH_HISTORY_TRACKLIST, json)
            .apply()
    }

    companion object {
        private const val KEY_SEARCH_HISTORY_TRACKLIST = "KEY_SEARCH_HISTORY_TRACKLIST"
        private const val SEARCH_HISTORY_SIZE = 10
    }
}