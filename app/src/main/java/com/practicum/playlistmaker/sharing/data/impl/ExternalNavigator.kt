package com.practicum.playlistmaker.sharing.data.impl

import android.app.Application
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigator(val app: Application) {

    fun shareLink(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/url"
        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(shareIntent)
    }

    fun openEmail(email: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)

        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.emailReceiver))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, email.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, email.message)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(supportIntent)
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        app.startActivity(intent)
    }


}