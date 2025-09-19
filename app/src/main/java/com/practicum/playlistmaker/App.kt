package com.practicum.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val NIGHTMODE_KEY = "key_for_night_mode"
class App : Application() {
    var darkTheme = false
    lateinit var nightModeSettingsInteractorImpl: NightModeSettingsInteractor
    override fun onCreate() {
        super.onCreate()

        // инициализация для последующего обращения к SharedPrefs
        Creator.initApplication(this)
        nightModeSettingsInteractorImpl = Creator.provideNightModeSettingsInteractor()



        //проверяем, есть ли в sharedPrefs ключ с темой, если нет - устанавливаем вслед за системой
        if (!nightModeSettingsInteractorImpl.checkIfDarkThemeIsSetInSharedPrefs()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        //если ключ есть, то устанавливаем нужную тему
        } else {
            switchTheme(nightModeSettingsInteractorImpl.getNightMode(checkIfDarkThemeEnabledOnDevice()))
        }
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    //при изменении конфигурации приложения  - (изменении темы устройства), проверяем есть ли ключ
    // в sharedPrefs, если нет, то используем текущую тему системы для передачи переключателю Свитч
    // на экране Настроек. Весь экран Настроек перерисовыается в зависимости
    // от темы устройства (кроме положения переключателя)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (!nightModeSettingsInteractorImpl.checkIfDarkThemeIsSetInSharedPrefs()) {
            darkTheme = checkIfDarkThemeEnabledOnDevice()
        }
    }

    //проверка темы устройства
    private fun checkIfDarkThemeEnabledOnDevice(): Boolean {
        return when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }
}

