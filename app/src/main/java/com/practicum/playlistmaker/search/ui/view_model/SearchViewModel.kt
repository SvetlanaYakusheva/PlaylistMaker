package com.practicum.playlistmaker.search.ui.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TrackListInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel (private val trackListInteractorImpl: TrackListInteractor, private val searchHistoryInteractorImpl: SearchHistoryInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null

    private var searchJob: Job? = null
    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }

    }

    fun getSearchHistory() {
        searchJob?.cancel()
        latestSearchText = "" //при вызове блока с историей обнуляем последний текстовый запрос,
        // чтобы при новом поиске по тем же символам не попасть в первый if
        //handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        searchHistoryInteractorImpl.getSearchHistory(
            consumer = object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(foundSearchHistoryTracks: List<Track>) {
                    renderState(
                        if (foundSearchHistoryTracks.isNotEmpty()) {
                            SearchState.ContentHistory(
                                foundSearchHistoryTracks = foundSearchHistoryTracks,
                            )
                        } else {
                            SearchState.ClearActivity
                        }
                    )
                }
            }
        )
    }

    fun addTrackToSearchHistory(track: Track) {
        searchHistoryInteractorImpl.addTrackToSearchHistory(track)
    }
    fun clearSearchHistory() {
        searchHistoryInteractorImpl.clearHistory()
        getClearActivity()
    }

    fun getClearActivity() {
        renderState(
            SearchState.ClearActivity
        )
        searchJob?.cancel()
    }


    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )

            viewModelScope.launch {
                trackListInteractorImpl
                    .search(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        } else {
            getSearchHistory()
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {

        if (foundTracks != null) {
            if (foundTracks.isEmpty()) {
                renderState(SearchState.Empty)
            } else {
                renderState(
                    SearchState.Content(foundTracks = foundTracks)
                )
            }
        } else {
            renderState(SearchState.Error)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }
}