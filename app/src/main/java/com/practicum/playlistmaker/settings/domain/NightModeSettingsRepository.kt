package com.practicum.playlistmaker.settings.domain

interface NightModeSettingsRepository {
    fun saveNightMode(nightMode: Boolean)
    fun getNightMode(defValue:Boolean): Boolean

    //проверка, есть ли в sharedPrefs ключ для темной темы
    fun checkIfDarkThemeIsSetInSharedPrefs(): Boolean

    fun getThemeState(defValue:Boolean): Boolean
}