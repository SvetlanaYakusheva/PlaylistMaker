package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.data.NightModeSettingsRepositoryImpl
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.TrackListRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.NightModeSettingsInteractor
import com.practicum.playlistmaker.domain.api.NightModeSettingsRepository
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.domain.api.TrackListInteractor
import com.practicum.playlistmaker.domain.api.TrackListRepository
import com.practicum.playlistmaker.domain.impl.NightModeSettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.TrackListInteractorImpl
import com.practicum.playlistmaker.presentation.PLAYLIST_MAKER_PREFERENCES

object Creator {
    private lateinit var application: Application

    fun initApplication (application: Application) {
        this.application = application
    }
    private fun provideSharedPreferences() : SharedPreferences {
        return  application.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
    }

    private fun getTrackListRepository(): TrackListRepository {
        return TrackListRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackListInteractor(): TrackListInteractor {
        return TrackListInteractorImpl(getTrackListRepository())
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences())
    }
    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    private fun getNightModeSettingsRepository() : NightModeSettingsRepository{
        return NightModeSettingsRepositoryImpl(provideSharedPreferences())
    }
    fun provideNightModeSettingsInteractor() : NightModeSettingsInteractor {
        return NightModeSettingsInteractorImpl(getNightModeSettingsRepository())
    }

}