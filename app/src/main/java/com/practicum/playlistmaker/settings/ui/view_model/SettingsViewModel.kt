package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractorImpl: NightModeSettingsInteractor,
    private val sharingInteractorImpl: SharingInteractor
) : ViewModel() {
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

}
