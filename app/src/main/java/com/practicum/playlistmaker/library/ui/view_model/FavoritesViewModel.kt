package com.practicum.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    private val temp = 0
    private val tempLiveData = MutableLiveData(temp)
    fun observetemp(): LiveData<Int> = tempLiveData
}