package com.practicum.playlistmaker.util

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.practicum.playlistmaker.settings.data.impl.NightModeSettingsRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackListRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor
import com.practicum.playlistmaker.settings.domain.NightModeSettingsRepository
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TrackListInteractor
import com.practicum.playlistmaker.search.domain.api.TrackListRepository
import com.practicum.playlistmaker.settings.domain.impl.NightModeSettingsInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TrackListInteractorImpl
import com.practicum.playlistmaker.PLAYLIST_MAKER_PREFERENCES
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
     private lateinit var application: Application

    fun initApplication (application: Application) {
        Creator.application = application
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

    private fun getNightModeSettingsRepository(): NightModeSettingsRepository {
        return NightModeSettingsRepositoryImpl(provideSharedPreferences())
    }

    fun provideNightModeSettingsInteractor(): NightModeSettingsInteractor {
        return NightModeSettingsInteractorImpl(getNightModeSettingsRepository())
    }

    private fun provideExternalNavigator(): ExternalNavigator {
        return ExternalNavigator()
    }
    fun provideSharingInteractor() : SharingInteractor {
        return SharingInteractorImpl(provideExternalNavigator())
    }

    fun provideApplication() : Application {
        return  application
    }
}