package com.practicum.playlistmaker.search.data.dto

data class TrackListResponse (val resultCount: Int,
                              val results: ArrayList<TrackDto>) : Response()
