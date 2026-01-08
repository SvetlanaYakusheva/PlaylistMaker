package com.practicum.playlistmaker.newplaylist.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.newplaylist.ui.EditPlaylistState
import com.practicum.playlistmaker.newplaylist.ui.presentation.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    private var playlistId: Int = 0
    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylistFragmentTitle.text = resources.getString(R.string.edit)
        binding.createPlaylistButton.text = resources.getString(R.string.save)
        playlistId = requireArguments().getInt(KEY_ID_PLAYLIST)

        // добавление слушателя для обработки нажатия на кнопку Back
        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().navigateUp()
            }
        })

        binding.backButton.setOnClickListener {
                findNavController().navigateUp()
        }

        binding.createPlaylistButton.setOnClickListener {
            viewModel.onUpdatePlaylistButtonClicked(
                playlistId,
                binding.inputEditTextPlaylistName.text.toString(),
                binding.inputEditTextPlaylistDescription.text.toString(),
                imageCoverUri
            )
            findNavController().navigateUp()
        }
        viewModel.observeEditPlaylistState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render (state: EditPlaylistState) {
        when (state) {
            is EditPlaylistState.Content -> showContent(state.playlist)

            is EditPlaylistState.Empty -> showEmpty()

        }
    }
    private fun showContent(playlist: Playlist) {
        binding.apply {
            Glide.with(requireContext())
                .load(Uri.parse(playlist.coverImageUri))
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_45)
                .into(newPlaylistCover)

            inputEditTextPlaylistName.setText(playlist.name)
            inputEditTextPlaylistDescription.setText(playlist.description)
        }
    }

    private fun showEmpty() {
        binding.apply {
            inputEditTextPlaylistName.setText("")
        }
    }

    companion object {
        const val KEY_ID_PLAYLIST = "KEY_ID_PLAYLIST"

        fun createArgs(id: Int): Bundle =
            bundleOf(KEY_ID_PLAYLIST to id)
    }
}