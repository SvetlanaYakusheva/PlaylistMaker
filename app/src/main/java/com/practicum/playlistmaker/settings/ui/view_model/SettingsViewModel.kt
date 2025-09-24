package com.practicum.playlistmaker.settings.ui.view_model


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.util.Creator


class SettingsViewModel(
    context: Context
) : ViewModel() {
    private val settingsInteractorImpl = Creator.provideNightModeSettingsInteractor()
    private val sharingInteractorImpl = Creator.provideSharingInteractor()
    private val stateLiveData = MutableLiveData<Boolean>()

    init {
        loadData()
    }

    fun observeState(): LiveData<Boolean> = stateLiveData

    fun changeNightMode(switcherChecked: Boolean, switcherPressed: Boolean) {
        if (switcherPressed) {
            stateLiveData.postValue(switcherChecked)
            settingsInteractorImpl.saveNightMode(switcherChecked)
        } else {
            stateLiveData.postValue(settingsInteractorImpl.getThemeState(true))
        }

    }
    private fun loadData() {
        stateLiveData.value = settingsInteractorImpl.getThemeState(false)
    }

    fun onShareLinkClickEvent() {
        sharingInteractorImpl.shareApp()
    }
    fun onWriteToSupportClick() {
        sharingInteractorImpl.openSupport()
    }
    fun onAgreementLinkClick() {
        sharingInteractorImpl.openTerms()
    }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }

//    private val onSharingClickEvent = SingleEventLiveData<SharingState>()
//    fun  getOnSharingClickEvent() : LiveData<SharingState> = onSharingClickEvent
//
//    fun showProductDetails(state: SharingState) {
//        onSharingClickEvent.value = state
//    }
}
