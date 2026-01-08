package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.favorites.data.TrackDbConvertor
import com.practicum.playlistmaker.library.favorites.data.impl.FavoritesRepositoryImpl
import com.practicum.playlistmaker.library.favorites.domain.FavoritesRepository
import com.practicum.playlistmaker.library.playlists.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.library.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackListRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.TrackListRepository
import com.practicum.playlistmaker.settings.data.impl.NightModeSettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.NightModeSettingsRepository
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {

    factory { TrackDbConvertor() }

    single<TrackListRepository> {
        TrackListRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get(), get())
    }

    single<NightModeSettingsRepository> {
        NightModeSettingsRepositoryImpl(get(), get())
    }

    single<ExternalNavigator> {
        ExternalNavigator(get())
    }

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(),get(), get(), get())
    }
}