package com.practicum.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {
        val iTunesBaseUrl = "https://itunes.apple.com"

        Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
      RetrofitNetworkClient(get())
    }


}

