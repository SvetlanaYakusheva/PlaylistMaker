package com.practicum.playlistmaker.settings.ui.view_model


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.sharing.SingleEventLiveData
import com.practicum.playlistmaker.util.Creator


class SettingsViewModel(
    context: Context
) : ViewModel() {
    private val settingsInteractorImpl = Creator.provideNightModeSettingsInteractor()
    private val sharingInteractorImpl = Creator.provideSharingInteractor()


    private var darkTheme = (context.applicationContext as App).darkTheme
    private val stateLiveData = MutableLiveData(darkTheme)

    fun observeState(): LiveData<Boolean> = stateLiveData

    fun changeNightMode(switcherChecked: Boolean) {
        darkTheme = switcherChecked
        stateLiveData.postValue(darkTheme)
        settingsInteractorImpl.saveNightMode(darkTheme)

    }

    fun getNightModeFromApp() {
    }

    private val onSharingClickEvent = SingleEventLiveData<Int>()
    fun getSharingClickEvent() : LiveData<Int> = onSharingClickEvent

//    fun getSharingClick() {
//        onSharingClickEvent.value =
//    }
//    val shareLinkClickEvent = SingleEventLiveData<String>()
//    fun onShareLinkClickEvent(url: String) {
//        shareLinkClickEvent.value = url
//    }
    fun onShareLinkClickEvent() {
        sharingInteractorImpl.shareApp()
    }

 //   val writeToSupportEvent = SingleEventLiveData<EmailData>()
//    fun onWriteToSupportClick(emaiLData: EmailData) {
//        writeToSupportEvent.value = emaiLData
//    }
    fun onWriteToSupportClick() {
        sharingInteractorImpl.openSupport()
    }

 //   val openUrlEvent = SingleEventLiveData<String>()

//    fun onLinkClick(url: String) {
//        openUrlEvent.value = url
//    }
    fun onAgreementLinkClick() {
        sharingInteractorImpl.openTerms()
    }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SettingsViewModel(app)
            }
        }
    }
}
