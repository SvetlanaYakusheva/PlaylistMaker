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
//        // mock - объект для проверки пустых полей
//        trackList.add(0, Track(111, "song1", "artist",4540,"url",null,null,"rock","russia"))
//        trackList.add(1, Track(222, "song2", "artist",4540,"url",null,"1988","rock","russia"))
//        trackList.add(2, Track(333, "song3", "artist",4540,"url","album",null,"rock","russia"))
//        trackList.add(3, Track(333, "song4", "artist",4540,"url","album","Э22222","rock","russia"))


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