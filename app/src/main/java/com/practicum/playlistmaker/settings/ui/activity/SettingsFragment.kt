package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeNightMode(checked, switcher.isPressed)
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

    override fun onDestroyView() {
        super.onDestroyView()
        //обнуление привязки во избежание утечки
        _binding = null
    }
}