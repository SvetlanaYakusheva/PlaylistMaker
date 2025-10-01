package com.practicum.playlistmaker.settings.ui.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.observeState().observe(this) {
            binding.themeSwitcher.isChecked = it
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->

                viewModel.changeNightMode(checked, switcher.isPressed)
        }

        binding.backButtonSettingsActivity.setOnClickListener {
            finish()
        }

       binding.shareButton.setOnClickListener {
            viewModel.onShareLinkClickEvent()
       }
        binding.supportButton.setOnClickListener {
            viewModel.onWriteToSupportClick()
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.onAgreementLinkClick()
        }

    }
}




