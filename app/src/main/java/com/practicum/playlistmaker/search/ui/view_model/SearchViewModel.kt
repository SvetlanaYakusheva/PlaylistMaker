package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.TrackListInteractor
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.search.ui.SearchState
import com.practicum.playlistmaker.util.Resource

class SearchViewModel (private val trackListInteractorImpl: TrackListInteractor, private val searchHistoryInteractorImpl: SearchHistoryInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun getSearchHistory() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        searchHistoryInteractorImpl.getSearchHistory(
            consumer = object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(foundSearchHistoryTracks: List<Track>) {
                    renderState(
                        SearchState.ContentHistory(
                            foundSearchHistoryTracks = foundSearchHistoryTracks,
                        )
                    )
                }
            }
        )
    }

    fun clearSearchHistory() {
        searchHistoryInteractorImpl.clearHistory()
        getClearActivity()
    }

    fun getClearActivity() {
        renderState(
            SearchState.ClearActivity
        )
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }
    fun addTrackToSearchHistory(track: Track) {
        searchHistoryInteractorImpl.addTrackToSearchHistory(track)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )

            trackListInteractorImpl.search(newSearchText, object : TrackListInteractor.TrackListConsumer {
                override fun consume(foundTracks: Resource<List<Track>>) {
                    handler.post {
                        if (foundTracks is Resource.Error) {
                            renderState(SearchState.Error)
                        } else if (foundTracks is Resource.Success) {
                            if (foundTracks.data.isEmpty()) {
                                renderState(SearchState.Empty)
                            } else {
                                renderState(
                                    SearchState.Content(foundTracks = foundTracks.data)
                                )
                            }
                        }
                    }
                }
            })
        } else {
            getSearchHistory()
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}