package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.favorites.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.favorites.domain.impl.FavoritesInteractorImpl
import com.practicum.playlistmaker.library.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.library.playlists.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TrackListInteractor
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TrackListInteractorImpl
import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.NightModeSettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<TrackListInteractor> {
        TrackListInteractorImpl(get())

    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<NightModeSettingsInteractor> {
        NightModeSettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}