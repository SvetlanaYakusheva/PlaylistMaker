package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.util.Creator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    val app = Creator.provideApplication()

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

