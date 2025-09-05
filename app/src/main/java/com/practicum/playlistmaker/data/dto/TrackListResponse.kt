package com.practicum.playlistmaker.data.dto

data class TrackListResponse (val resultCount: Int,
                              val results: ArrayList<TrackDto>) : Response()
