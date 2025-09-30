package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val NIGHTMODE_KEY = "key_for_night_mode"
class App : Application() {
        private var darkTheme = true
        lateinit var nightModeSettingsInteractorImpl: NightModeSettingsInteractor
        override fun onCreate() {
            super.onCreate()

            //удаляем ключ с темой , чтобы проверять проблемную ситуацию -  учетом первого входа
//            val sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
//            sharedPreferences.edit()
//                .remove(NIGHTMODE_KEY)
//                .apply()

            // инициализация для последующего обращения к SharedPrefs
            Creator.initApplication(this)
            nightModeSettingsInteractorImpl = Creator.provideNightModeSettingsInteractor()

            darkTheme = nightModeSettingsInteractorImpl.getThemeState(darkTheme)
        }

}
