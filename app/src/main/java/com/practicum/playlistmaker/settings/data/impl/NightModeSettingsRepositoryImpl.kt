package com.practicum.playlistmaker.settings.data.impl

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.NightModeSettingsRepository
import com.practicum.playlistmaker.NIGHTMODE_KEY

class NightModeSettingsRepositoryImpl (private val sharedPreferences: SharedPreferences, val app: Application) :
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

    override fun getThemeState(defValue: Boolean) : Boolean {
        if (checkIfDarkThemeIsSetInSharedPrefs()) {
            switchTheme(sharedPreferences.getBoolean(NIGHTMODE_KEY, defValue))
            return sharedPreferences.getBoolean(NIGHTMODE_KEY, defValue)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            return checkIfDarkThemeEnabledOnDevice()
        }
    }

    //проверка темы устройства
    private fun checkIfDarkThemeEnabledOnDevice(): Boolean {
        return when (app.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }
    private fun switchTheme(darkThemeEnabled: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}