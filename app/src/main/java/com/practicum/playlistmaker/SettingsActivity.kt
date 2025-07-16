package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        themeSwitcher.isChecked = (applicationContext as App).darkTheme
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            if (switcher.isPressed) {
                (applicationContext as App).switchTheme(checked)
                sharedPrefs.edit()
                    .putBoolean(NIGHTMODE_KEY, checked)
                    .apply()
            } else {
                themeSwitcher.isChecked = (applicationContext as App).darkTheme
            }
        }


        val buttonBackToMainActivity = findViewById<ImageButton>(R.id.back_button_SettingsActivity)
        buttonBackToMainActivity.setOnClickListener {
            finish()
        }

        val buttonShare = findViewById<FrameLayout>(R.id.share_button)
        buttonShare.setOnClickListener {
            val message = resources.getString(R.string.shared_app_url)
            val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type="text/url"
                shareIntent.putExtra(Intent.EXTRA_TEXT, message)
                startActivity(shareIntent)
        }

        val buttonSupport = findViewById<FrameLayout>(R.id.support_button)
        buttonSupport.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)

            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.supportEmail)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subjectOfEmailToSupport))
            supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messageToSupport))

            startActivity(supportIntent)
        }

        val buttonUserAgreement = findViewById<FrameLayout>(R.id.userAgreement_button)
        buttonUserAgreement.setOnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.urlAgreement)))

            startActivity(agreementIntent)
        }
    }

}