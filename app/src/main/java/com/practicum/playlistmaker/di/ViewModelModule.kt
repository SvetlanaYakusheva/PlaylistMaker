package com.practicum.playlistmaker.di


import com.practicum.playlistmaker.library.favorites.ui.view_model.FavoritesViewModel
import com.practicum.playlistmaker.library.playlists.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.library.playlists.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.newplaylist.ui.presentation.EditPlaylistViewModel
import com.practicum.playlistmaker.newplaylist.ui.presentation.NewPlaylistViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get())
    }

        viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel { (id: Int) ->
        PlaylistViewModel(id, get(), get())
    }

    viewModel {( id: Int) ->
        EditPlaylistViewModel(id, get())
    }
}

