package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.NightModeSettingsInteractor
import com.practicum.playlistmaker.domain.api.NightModeSettingsRepository


class NightModeSettingsInteractorImpl (private val repository: NightModeSettingsRepository) :
    NightModeSettingsInteractor {
    override fun saveNightMode(nightMode: Boolean) {
        repository.saveNightMode(nightMode)
    }

    override fun getNightMode(defValue:Boolean): Boolean {
        return repository.getNightMode(defValue)
    }

    override fun checkIfDarkThemeIsSetInSharedPrefs(): Boolean {
        return repository.checkIfDarkThemeIsSetInSharedPrefs()
    }

}