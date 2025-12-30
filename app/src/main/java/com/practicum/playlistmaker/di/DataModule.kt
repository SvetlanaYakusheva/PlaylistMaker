package com.practicum.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.library.favorites.data.db.FavoritesDatabase
import com.practicum.playlistmaker.library.playlists.data.PlaylistDbConverter
import com.practicum.playlistmaker.library.playlists.data.db.PlaylistDatabase
import com.practicum.playlistmaker.library.playlists.data.db.SummaryPlaylistDatabase
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.ITunesApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {

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

    single {
        Room.databaseBuilder(androidContext(), FavoritesDatabase::class.java, "database.db")
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), PlaylistDatabase::class.java, "databasePlaylist.db")
            .build()
    }
    single {
        Room.databaseBuilder(androidContext(), SummaryPlaylistDatabase::class.java, "databaseSummaryPlaylist.db")
            .build()
    }

    single<PlaylistDbConverter> {
        PlaylistDbConverter(get()) }
}

const val iTunesBaseUrl = "https://itunes.apple.com"

