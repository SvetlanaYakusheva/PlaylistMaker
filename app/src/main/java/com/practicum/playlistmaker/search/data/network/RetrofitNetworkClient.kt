package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackListRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient (private val iTunesService: ITunesApi) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is TrackListRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {

            try {
                val response = iTunesService.search(dto.expression)

                response.apply { resultCode = if (response.results.isEmpty()) 404 else 200 }
            } catch (ex: Exception) {
                Response().apply { resultCode = 400 }
            }

        }
    }
}