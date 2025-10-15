package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackListRequest

class RetrofitNetworkClient (private val iTunesService: ITunesApi) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackListRequest) {
            try {
                val resp = iTunesService.search(dto.expression).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } catch (ex: Exception) {
                return Response().apply { resultCode = 400 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}