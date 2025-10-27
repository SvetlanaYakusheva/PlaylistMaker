package com.practicum.playlistmaker.search.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerFragment
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private val handler = Handler(Looper.getMainLooper())
    private var textWatcher: TextWatcher? = null

    private val trackClickListener = TrackAdapter.TrackClickListener { track ->
        // обработка нажатия на трек - добавление в историю поиска
        viewModel.addTrackToSearchHistory(track)
        // открытие экрана Аудиоплеера при нажатии на трек в списке
        // с защитой от повторного нажатия в теч 1 сек
        if (clickDebounce()) {
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }
    }

    private var isClickAllowed = true
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private var trackAdapter = TrackAdapter(trackClickListener)
    private var trackAdapterSearchHistory  = TrackAdapter(trackClickListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.refreshButton.setOnClickListener{
            viewModel.searchDebounce(
                changedText = binding.inputEditText.text.toString()
            )
        }

        binding.buttonClearSearchActivity.setOnClickListener {
            binding.inputEditText.clearFocus()
            binding.inputEditText.setText("")
            val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)

            viewModel.getClearActivity()
        }

        //блок с историей поиска
        binding.buttonClearSearchHistory.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.searchHistoryRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchHistoryRecycler.adapter = trackAdapterSearchHistory

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonClearSearchActivity.visibility = clearButtonVisibility(s)
                if (binding.inputEditText.hasFocus() && s?.isEmpty() == true  && trackAdapterSearchHistory.trackList.isNotEmpty()) {
                    viewModel.getSearchHistory()
                } else if (binding.inputEditText.hasFocus() && s?.isEmpty() == true  && trackAdapterSearchHistory.trackList.isEmpty()) {
                    viewModel.getClearActivity()
                }
                if (binding.inputEditText.hasFocus() && s?.isEmpty()==false) {
                    viewModel.searchDebounce(
                        changedText = s.toString()
                    )
                }
            }
            override fun afterTextChanged(s: Editable?) { }
        }
        binding.inputEditText.addTextChangedListener(textWatcher)

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty()) {
                viewModel.getSearchHistory()
            }
        }

        binding.recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = trackAdapter

        //слушатель нажатия Enter на вирт клавиатуре
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.inputEditText.text.isNotEmpty()) {
                    viewModel.searchDebounce(
                        changedText = binding.inputEditText.text.toString()
                    )
                } else {
                    if (trackAdapterSearchHistory.trackList.isNotEmpty()) {
                        viewModel.getSearchHistory()
                    } else if ((trackAdapter.trackList.isNotEmpty() or binding.errorImage.isVisible)) {
                        viewModel.getClearActivity()
                    }
                }
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //очищаем очередь при закрытии экрана
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        //обнуление привязки во избежание утечки
        _binding = null
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.foundTracks)
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
            is SearchState.ContentHistory -> showContentHistory(state.foundSearchHistoryTracks)
            is SearchState.ClearActivity -> showClearActivity()
        }
    }

    private fun showClearActivity(){
        binding.apply {
            progressBar.visibility = View.GONE
            recycler.visibility = View.GONE
            errorImage.visibility = View.GONE
            errorMessage.visibility = View.GONE
            errorMessageDescription.visibility = View.GONE
            refreshButton.visibility = View.GONE
            searchHistoryLayout.visibility = View.GONE

        }
    }
    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            errorImage.visibility = View.GONE
            errorMessage.visibility = View.GONE
            errorMessageDescription.visibility = View.GONE
            refreshButton.visibility = View.GONE
            searchHistoryLayout.visibility = View.GONE

        }
    }

    private fun showContent(trackList: List<Track>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            errorImage.visibility = View.GONE
            errorMessage.visibility = View.GONE
            errorMessageDescription.visibility = View.GONE
            refreshButton.visibility = View.GONE
            searchHistoryLayout.visibility = View.GONE
        }
        trackAdapter.trackList.clear()
        trackAdapter.trackList.addAll(trackList)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showError() {
        binding.apply {
            progressBar.visibility = View.GONE
            recycler.visibility = View.GONE
            errorImage.visibility = View.VISIBLE
            errorMessage.visibility = View.VISIBLE
            errorMessageDescription.visibility = View.VISIBLE
            refreshButton.visibility = View.VISIBLE
            searchHistoryLayout.visibility = View.GONE

            errorImage.setImageResource(R.drawable.ic_network_error_120)
            errorMessage.text = getString(R.string.something_went_wrong)
            errorMessageDescription.text =
                getString(R.string.something_went_wrong_description)
        }
    }

    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            recycler.visibility = View.GONE
            errorImage.visibility = View.VISIBLE
            errorMessage.visibility = View.VISIBLE
            errorMessageDescription.visibility = View.GONE
            refreshButton.visibility = View.GONE
            searchHistoryLayout.visibility = View.GONE

            errorImage.setImageResource(R.drawable.ic_error_nothing_found_120)
            errorMessage.text =getString(R.string.nothing_found)
        }
    }

    private fun showContentHistory(trackList: List<Track>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recycler.visibility = View.GONE
            errorImage.visibility = View.GONE
            errorMessage.visibility = View.GONE
            errorMessageDescription.visibility = View.GONE
            refreshButton.visibility = View.GONE
            searchHistoryLayout.visibility = View.VISIBLE
        }
        trackAdapterSearchHistory.trackList.clear()
        trackAdapterSearchHistory.trackList.addAll(trackList)
        trackAdapterSearchHistory.notifyDataSetChanged()

        if (trackList.isEmpty()) binding.searchHistoryLayout.visibility = View.GONE
    }
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }
}