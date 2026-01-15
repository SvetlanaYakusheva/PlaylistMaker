package com.practicum.playlistmaker.library.playlists.ui.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.library.playlists.ui.PlaylistState
import com.practicum.playlistmaker.library.playlists.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.newplaylist.ui.activity.EditPlaylistFragment
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int = 0
    private val viewModel: PlaylistViewModel by viewModel {
        parametersOf(playlistId)
    }
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private lateinit var onTrackLongClickDebounce: (Track) -> Unit
    private var trackAdapter: TrackInPlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val behavior = BottomSheetBehavior.from(binding.playlistBottomSheet)
        //расчет высоты bottomsheet относительно других view для конкретного экрана
        binding.upperLayout.post {
            val location = IntArray(2)
            binding.upperLayout.getLocationOnScreen(location)

            val screenHeight = resources.displayMetrics.heightPixels
            // Расстояние от низа экрана до нижней границы targetView
            val targetPeekHeight = screenHeight - (location[1] + binding.upperLayout.height)

            behavior.peekHeight = targetPeekHeight
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        // добавление слушателя для обработки нажатия на кнопку Back
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        playlistId = requireArguments().getInt(KEY_ID_PLAYLIST)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        onTrackLongClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            showDialog({ viewModel.deleteTrackFromPlaylist(track) },
                        requireContext().getString(R.string.do_you_wanna_delete_track),
                        "",
                        requireContext().getString(R.string.yes),
                        requireContext().getString(R.string.no))
        }

        trackAdapter = TrackInPlaylistAdapter (
            {track -> onTrackClickDebounce(track)},
            {track -> onTrackLongClickDebounce(track)}
        )
        binding.recyclerPlaylist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerPlaylist.adapter = trackAdapter

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.shareButton.setOnClickListener {
            if (trackAdapter?.trackList?.isEmpty() == true) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.toast_no_tracks_in_playlist_to_share), Toast.LENGTH_LONG).show()
            } else {
                viewModel.onShareButtonClickEvent()
            }

        }

        val overlay = binding.overlay
        val bottomSheetContainer = binding.playlistMenuBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.menuButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        binding.shareButtonBottomsheet.setOnClickListener {
            if (trackAdapter?.trackList?.isEmpty() == true) {
                Toast.makeText(requireContext(), requireContext().getString(R.string.toast_no_tracks_in_playlist_to_share), Toast.LENGTH_LONG).show()
            } else {
                viewModel.onShareButtonClickEvent()
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.editButtonBottomsheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlistId)
            )
        }

        binding.deleteButtonBottomsheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            showDialog(
                { viewModel.onDeleteButtonClickEvent()
                    findNavController().navigateUp()},
                requireContext().getString(R.string.delete_playlist),
                requireContext().getString(R.string.are_you_sure_to_delete_playlistP1) +
                        binding.playlistName.text +
                        requireContext().getString(R.string.are_you_sure_to_delete_playlistP2),
                requireContext().getString(R.string.delete),
                requireContext().getString(R.string.cancel)
            )



        }
    }
    private fun render (state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlist, state.foundtracks, state.playlistDuration)
            is PlaylistState.Empty -> showEmpty(state.playlist)

        }
    }
    private fun showContent(playlist: Playlist, tracks: List<Track>, duration: String) {
        binding.apply {
            Glide.with(requireContext())
                .load(Uri.parse(playlist.coverImageUri))
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_45)
                .into(playlistCover)

            playlistName.text = playlist.name
            playlistDescription.text = playlist.description

            playlistDuration.text = resources.getQuantityString(R.plurals.numberOfMinutes, duration.toInt(), duration.toInt())
            playlistSize.text= resources.getQuantityString(R.plurals.numberOfTracks, playlist.playlistSize, playlist.playlistSize)

            recyclerPlaylist.isVisible = true
            yourPlaylistIsEmptyTextview.isVisible = false

            Glide.with(requireContext())
                .load(Uri.parse(playlist.coverImageUri))
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_45)
                .into(playlistInfoInMenu.playlistCover)

            playlistInfoInMenu.playlistName.text = playlist.name
            playlistInfoInMenu.playlistSize.text = resources.getQuantityString(R.plurals.numberOfTracks, playlist.playlistSize, playlist.playlistSize)
        }

        trackAdapter?.trackList?.clear()
        trackAdapter?.trackList?.addAll(tracks)
        trackAdapter?.notifyDataSetChanged()
    }
    private fun showEmpty(playlist: Playlist) {
        binding.apply {
            Glide.with(requireContext())
                .load(Uri.parse(playlist.coverImageUri))
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_45)
                .into(playlistCover)

            playlistName.text = playlist.name
            playlistDescription.text = playlist.description
            playlistDuration.text = resources.getQuantityString(R.plurals.numberOfMinutes, 0, 0)


            playlistSize.text = resources.getQuantityString(R.plurals.numberOfTracks, playlist.playlistSize, playlist.playlistSize)
            recyclerPlaylist.isVisible = false
            yourPlaylistIsEmptyTextview.isVisible = true

            Glide.with(requireContext())
                .load(Uri.parse(playlist.coverImageUri))
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder_45)
                .into(playlistInfoInMenu.playlistCover)
            playlistInfoInMenu.playlistName.text = playlist.name

            playlistInfoInMenu.playlistSize.text = resources.getQuantityString(R.plurals.numberOfTracks, playlist.playlistSize, playlist.playlistSize)
        }
        trackAdapter?.trackList?.clear()
        trackAdapter?.notifyDataSetChanged()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        //обнуление привязки во избежание утечки
        _binding = null
    }


    private fun showDialog( runnable: () -> Unit, title: String, msg: String, yes: String, no: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton(yes) { dialog, which ->
               runnable()
            }
            .setNegativeButton(no) { dialog, which ->

            }
            .show()
    }

    companion object {
        const val KEY_ID_PLAYLIST = "KEY_ID_PLAYLIST"
        private const val CLICK_DEBOUNCE_DELAY = 2000L

        fun createArgs(id: Int): Bundle =
            bundleOf(KEY_ID_PLAYLIST to id)
    }
}