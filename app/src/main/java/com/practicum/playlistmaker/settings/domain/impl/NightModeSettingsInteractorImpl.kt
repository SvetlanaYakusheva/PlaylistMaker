package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor
import com.practicum.playlistmaker.settings.domain.NightModeSettingsRepository


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