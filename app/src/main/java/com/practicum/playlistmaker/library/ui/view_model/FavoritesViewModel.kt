package com.practicum.playlistmaker.library.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorites.domain.FavoritesInteractor
import com.practicum.playlistmaker.library.ui.FavoritesState
import com.practicum.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoritesViewModel (
                private val favoritesInteractor: FavoritesInteractor
): ViewModel() {

    private val stateLiveData = MutableLiveData<FavoritesState>()
    fun observeState(): LiveData<FavoritesState> = stateLiveData

    fun fillData() {

        viewModelScope.launch {
            favoritesInteractor
                .getFavoritesList()
                .collect { favoritesList ->
                    processResult(favoritesList)
                }
        }
    }
    
    private fun processResult(favoritesList: List<Track>) {
        if (favoritesList.isEmpty()) {
            renderState(FavoritesState.Empty)
        } else {
            renderState(FavoritesState.Content(favoritesList))
        }
    }

    private fun renderState(state: FavoritesState) {
        stateLiveData.postValue(state)
    }
}