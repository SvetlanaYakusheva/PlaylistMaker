package com.practicum.playlistmaker.sharing.domain.impl

import android.app.Application
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    val app: Application
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {

        return app.getString(R.string.shared_app_url)

    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            app.getString(R.string.supportEmail),
            app.getString(R.string.subjectOfEmailToSupport),
            app.getString(R.string.messageToSupport)
        )
    }

    private fun getTermsLink(): String {
        return app.getString(R.string.urlAgreement)
    }
}

