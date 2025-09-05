package com.practicum.playlistmaker.domain.api



interface NightModeSettingsInteractor {
    fun saveNightMode(nightMode: Boolean)
    fun getNightMode(defValue:Boolean): Boolean
    fun checkIfDarkThemeIsSetInSharedPrefs() : Boolean
}