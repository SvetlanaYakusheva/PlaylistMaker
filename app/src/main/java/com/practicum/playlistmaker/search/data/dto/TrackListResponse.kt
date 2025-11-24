package com.practicum.playlistmaker.search.data.dto

data class TrackListResponse (
    var resultCount: Int,
    val results: ArrayList<TrackDto>) : Response()
