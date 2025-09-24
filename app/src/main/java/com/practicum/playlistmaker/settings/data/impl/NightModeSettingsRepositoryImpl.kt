package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.NightModeSettingsRepository
import com.practicum.playlistmaker.NIGHTMODE_KEY

class NightModeSettingsRepositoryImpl (private val sharedPreferences: SharedPreferences) :
    NightModeSettingsRepository {
    override fun saveNightMode(nightMode: Boolean) {
        sharedPreferences.edit().putBoolean(NIGHTMODE_KEY, nightMode).apply()
        switchTheme(nightMode)
    }

    override fun getNightMode(defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(NIGHTMODE_KEY, defValue)
    }

    //проверка, есть ли в sharedPrefs ключ для темной темы
    override fun checkIfDarkThemeIsSetInSharedPrefs(): Boolean {
        return sharedPreferences.contains(NIGHTMODE_KEY)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        //darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}