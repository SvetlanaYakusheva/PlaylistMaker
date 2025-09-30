package com.practicum.playlistmaker.settings.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var viewModel: SettingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, SettingsViewModel.getFactory())
            .get(SettingsViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->

                viewModel?.changeNightMode(checked, switcher.isPressed)
        }

        binding.backButtonSettingsActivity.setOnClickListener {
            finish()
        }

       binding.shareButton.setOnClickListener {
            viewModel?.onShareLinkClickEvent()
       }
        binding.supportButton.setOnClickListener {
            viewModel?.onWriteToSupportClick()
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel?.onAgreementLinkClick()
        }

    }

//    private fun render(state: SharingState) {
//        when (state) {
//            is SharingState.ShareApp -> viewModel?.onShareLinkClickEvent()
//            is SharingState.Agreement -> viewModel?.onAgreementLinkClick()
//            is SharingState.SendEmail -> viewModel?.onWriteToSupportClick()
//        }
//    }
}




