package com.practicum.playlistmaker.domain.api


interface NightModeSettingsRepository {
    fun saveNightMode(nightMode: Boolean)
    fun getNightMode(defValue:Boolean): Boolean

    //проверка, есть ли в sharedPrefs ключ для темной темы
    fun checkIfDarkThemeIsSetInSharedPrefs(): Boolean
}