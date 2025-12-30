package com.practicum.playlistmaker.library.favorites.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.library.favorites.ui.FavoritesState
import com.practicum.playlistmaker.library.favorites.ui.view_model.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.activity.TrackAdapter
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private var trackAdapter: TrackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        trackAdapter = TrackAdapter { track ->
            onTrackClickDebounce(track)
        }
        binding.recyclerFavorites.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerFavorites.adapter = trackAdapter

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        //обнуление привязки во избежание утечки
        _binding = null
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> showContent(state.foundTracks)
            is FavoritesState.Empty -> showEmpty()

        }
    }
    private fun showContent(trackList: List<Track>) {
        binding.apply {

            recyclerFavorites.visibility = View.VISIBLE
            errorImageFragmentFavorites.visibility = View.GONE
            errorMessageFragmentFavorites.visibility = View.GONE

        }
        trackAdapter?.trackList?.clear()
        trackAdapter?.trackList?.addAll(trackList)
        trackAdapter?.notifyDataSetChanged()
    }
    private fun showEmpty() {
        binding.apply {

            recyclerFavorites.visibility = View.GONE
            errorImageFragmentFavorites.visibility = View.VISIBLE
            errorMessageFragmentFavorites.visibility = View.VISIBLE

        }
    }
    companion object {
        fun newInstance() = FavoritesFragment()

        private const val CLICK_DEBOUNCE_DELAY = 2000L

    }
}
