package com.practicum.playlistmaker.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.library.playlists.ui.PlaylistsState
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import com.practicum.playlistmaker.player.ui.PlayerState
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.util.dpToPx
import com.practicum.playlistmaker.util.getCoverArtwork
import com.practicum.playlistmaker.util.getDateFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    var track: Track? = null
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private var playlistAdapter: PlaylistBottomSheetAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        track = requireArguments().getParcelable(KEY_TRACK_TO_PLAYER, Track::class.java)

        binding.trackName.text = track?.trackName
        binding.artistName.text = track?.artistName
        binding.trackTime.text = getDateFormat(track?.trackTime)
        if (track?.collectionName == null) {
            binding.collectionName.visibility = View.GONE
            binding.textviewCollectionName.visibility = View.GONE
        } else {
            binding.collectionName.text = track!!.collectionName
        }
        if (track?.releaseDate == null) {
            binding.releaseDate.visibility = View.GONE
            binding.textviewReleaseDate.visibility = View.GONE
        } else {
            binding.releaseDate.text = track!!.releaseDate?.substring(0, 4)
        }
        binding.genre.text = track?.primaryGenreName
        binding.country.text = track?.country

        Glide.with(this)
            .load(getCoverArtwork(track?.artworkUrl100))
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, requireContext())))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(binding.trackCover)



        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            changeButtonImage(it is PlayerState.Playing)
            binding.timer.text = it.progress
        }

        binding.playButton.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        viewModel.observeFavoriteState().observe(viewLifecycleOwner) {
            if (track != null) {

                changeFavoritesButtonImage(track!!.isFavorite)
            }
        }

        binding.addToFavoritesButton.setOnClickListener {
            if (track != null) {
                viewModel.onFavoriteButtonClicked(track!!)
            }
        }

        val overlay = binding.overlay
        val bottomSheetContainer = binding.playerBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.addToQueueButton.setOnClickListener {
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

        binding.makePlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        playlistAdapter = PlaylistBottomSheetAdapter { playlist ->
            if (track != null) {

                viewModel.onPlaylistClicked(track!!, playlist)
            }
        }

        viewModel.observeAddToPlaylistState().observe(viewLifecycleOwner) {
            if (it.first != null) {
                if (it.first == false) {
                    Toast.makeText(
                        requireContext(),
                        "Трек уже добавлен в плейлист ${it.second}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    Toast.makeText(
                        requireContext(),
                        "Добавлено в плейлист ${it.second}",
                        Toast.LENGTH_LONG
                    ).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

        }
        binding.recyclerPlaylists.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerPlaylists.adapter = playlistAdapter

        viewModel.fillData()
        viewModel.observePlaylistsState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun changeButtonImage(isPlaying: Boolean) {
        if (isPlaying) {
            binding.playButton.setImageResource(R.drawable.pause_button_100)
        } else {
            binding.playButton.setImageResource(R.drawable.play_button_100)
        }
    }

    private fun changeFavoritesButtonImage(isFavorite: Boolean) {
        if (isFavorite) {
            binding.addToFavoritesButton.setImageResource(R.drawable.added_to_favorites_51)
        } else {
            binding.addToFavoritesButton.setImageResource(R.drawable.add_to_favorites_51)
        }
    }
    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //обнуление привязки во избежание утечки
        _binding = null
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> showContent(state.foundPlaylists)
            is PlaylistsState.Empty -> showEmpty()

        }
    }
    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            recyclerPlaylists.isVisible = true
        }

        playlistAdapter?.playlistList?.clear()
        playlistAdapter?.playlistList?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()
    }
    private fun showEmpty() {
        binding.apply {
            recyclerPlaylists.isVisible = false
        }
    }
    companion object {
        const val KEY_TRACK_TO_PLAYER = "KEY_TRACK_TO_PLAYER"

        fun createArgs(track: Track): Bundle =
            bundleOf(KEY_TRACK_TO_PLAYER to track)
    }
}

