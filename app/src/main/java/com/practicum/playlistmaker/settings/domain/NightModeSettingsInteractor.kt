package com.practicum.playlistmaker.settings.domain



interface NightModeSettingsInteractor {
    fun saveNightMode(nightMode: Boolean)
    fun getNightMode(defValue:Boolean): Boolean
    fun checkIfDarkThemeIsSetInSharedPrefs() : Boolean
}