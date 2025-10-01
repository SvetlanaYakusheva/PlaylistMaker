package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackListRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TrackListRepository
import com.practicum.playlistmaker.settings.data.impl.NightModeSettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.NightModeSettingsRepository
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {

    single<TrackListRepository> {
        TrackListRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<NightModeSettingsRepository> {
        NightModeSettingsRepositoryImpl(get(), get())
    }

    single<ExternalNavigator> {
        ExternalNavigator(get())
    }

}