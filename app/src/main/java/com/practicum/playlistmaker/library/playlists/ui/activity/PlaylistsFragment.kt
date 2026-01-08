package com.practicum.playlistmaker.library.playlists.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.playlists.ui.PlaylistsState
import com.practicum.playlistmaker.library.playlists.ui.view_model.PlaylistsViewModel
import com.practicum.playlistmaker.library.playlists.domain.model.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()

    private var playlistAdapter: PlaylistAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.makePlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }


        playlistAdapter = PlaylistAdapter { playlist ->
            findNavController().navigate(R.id.action_libraryFragment_to_playlistFragment,
                                        PlaylistFragment.createArgs(playlist.id))
        }

        binding.recyclerPlaylists.layoutManager =
            GridLayoutManager(requireContext(), /*Количество столбцов*/ 2)
        binding.recyclerPlaylists.adapter = playlistAdapter

        viewModel.fillData()
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
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
            noPlaylistsFound.isVisible = false
        }
        playlistAdapter?.playlistList?.clear()
        playlistAdapter?.playlistList?.addAll(playlists)
        playlistAdapter?.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.apply {
            recyclerPlaylists.isVisible = false
            noPlaylistsFound.isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //обнуление привязки во избежание утечки
        _binding = null
    }
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
