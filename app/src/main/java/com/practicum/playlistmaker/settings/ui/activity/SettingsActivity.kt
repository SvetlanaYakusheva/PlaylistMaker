package com.practicum.playlistmaker.settings.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var viewModel: SettingsViewModel? = null

    //private  lateinit var themeSwitcher: SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //themeSwitcher = findViewById(R.id.themeSwitcher)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())
            .get(SettingsViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        //themeSwitcher.isChecked = viewModel?.getNightModeFromApp()

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            if (switcher.isPressed) {
                viewModel?.changeNightMode(checked)
                //(applicationContext as App).switchTheme(checked)
//            } else {
//                viewModel?.getNightModeFromApp()

            }
        }

        viewModel?.getSharingClickEvent()?.observe(this) {

        }
        //val buttonBackToMainActivity = findViewById<ImageButton>(R.id.back_button_SettingsActivity)
        binding.backButtonSettingsActivity.setOnClickListener {
            finish()
        }

        //val buttonShare = findViewById<FrameLayout>(R.id.share_button)
        binding.shareButton.setOnClickListener {

            viewModel?.onShareLinkClickEvent()
        }
//        viewModel?.shareLinkClickEvent?.observe(this) { url ->
//            shareLink(url)
//        }

        //val buttonSupport = findViewById<FrameLayout>(R.id.support_button)
        binding.supportButton.setOnClickListener {

            viewModel?.onWriteToSupportClick()
        }
//        viewModel?.writeToSupportEvent?.observe(this) { emailData ->
//                sendEmailToSupport(emailData)
//        }

        //val buttonUserAgreement = findViewById<FrameLayout>(R.id.userAgreement_button)
        binding.userAgreementButton.setOnClickListener {
//            val urlToOpen = getString(R.string.urlAgreement) // Ваш URL
//            viewModel?.onLinkClick(urlToOpen)
            viewModel?.onAgreementLinkClick()
        }

//        viewModel?.openUrlEvent?.observe(this) { url ->
//                openUrlWithBrowser(url)
//        }
    }

//    private fun shareLink(url: String) {
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.type = "text/url"
//        shareIntent.putExtra(Intent.EXTRA_TEXT, url)
//        startActivity(shareIntent)
//    }
//
//    private fun sendEmailToSupport(emailData: EmailData) {
//        val supportIntent = Intent(Intent.ACTION_SENDTO)
//
//        supportIntent.data = Uri.parse("mailto:")
//        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.emailReciever))
//        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
//        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData.message)
//        startActivity(supportIntent)
//    }
//
////    private fun openUrlWithBrowser(url: String) {
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        startActivity(intent)
//    }
}




