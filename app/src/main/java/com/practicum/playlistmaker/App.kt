package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.NightModeSettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val NIGHTMODE_KEY = "key_for_night_mode"

class App : Application() {
        private var darkTheme = true
        lateinit var nightModeSettingsInteractorImpl: NightModeSettingsInteractor
        override fun onCreate() {
            super.onCreate()

            startKoin {
                androidContext(this@App)
                modules(dataModule, repositoryModule, interactorModule, viewModelModule)
            }

            nightModeSettingsInteractorImpl = getKoin().get<NightModeSettingsInteractor>()

            darkTheme = nightModeSettingsInteractorImpl.getThemeState(darkTheme)
        }

}
