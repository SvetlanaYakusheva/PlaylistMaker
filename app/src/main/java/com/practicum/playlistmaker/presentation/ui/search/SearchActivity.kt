package com.practicum.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TrackListInteractor
import com.practicum.playlistmaker.domain.models.Resource
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.audioplayer.AudioPlayerActivity


class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = ""
    private var trackList: MutableList<Track> = mutableListOf()
    private var searchHistoryTrackList: MutableList<Track> = mutableListOf()

    private val trackListInteractorImpl = Creator.provideTrackListInteractor()
    private val searchHistoryInteractorImpl = Creator.provideSearchHistoryInteractor()

    private lateinit var inputEditText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderMessageDescription: TextView
    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    
    private val trackClickListener = TrackAdapter.TrackClickListener {
        // обработка нажатия на трек - добавление в историю поиска
        searchHistoryInteractorImpl.addTrackToSearchHistory(it)

        // открытие экрана Аудиоплеера при нажатии на трек в списке
        // с защитой от повторного нажатия в теч 1 сек
        if (clickDebounce()) {
            val audioPlayerIntent = Intent(this, AudioPlayerActivity::class.java)
            audioPlayerIntent.putExtra(
                AudioPlayerActivity.KEY_TRACK_TO_AUDIOPLAYER,
                it
            )
            startActivity(audioPlayerIntent)
        }
    }
    private var trackAdapter = TrackAdapter(trackClickListener)
    private var trackAdapterSearchHistory  = TrackAdapter(trackClickListener)

    val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        search()
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val buttonBackToMainActivity = findViewById<ImageButton>(R.id.back_button_SearchActivity)
        buttonBackToMainActivity.setOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.button_clear_searchActivity)

        placeholderImage = findViewById(R.id.error_image)
        placeholderMessage = findViewById(R.id.error_message)
        placeholderMessageDescription = findViewById(R.id.error_message_description)
        refreshButton = findViewById(R.id.refresh_button)
        progressBar = findViewById(R.id.progressBar)

        refreshButton.setOnClickListener{
            search()
        }


        clearButton.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            clearState()
        }

        //блок с историей поиска
        val searchHistoryLayout = findViewById<ViewGroup>(R.id.search_history_layout)
        val clearHistoryButton = findViewById<Button>(R.id.button_clear_search_history)
        clearHistoryButton.setOnClickListener {
            searchHistoryInteractorImpl.clearHistory(
                consumer =  object : SearchHistoryInteractor.ClearHistoryConsumer {

                    override fun consume(unit: Unit) {
                        searchHistoryTrackList.clear()
                        trackAdapterSearchHistory.notifyDataSetChanged()
                        searchHistoryLayout.visibility = View.GONE
                    }
                }
            )
        }

        val recyclerViewSearchHistory = findViewById<RecyclerView>(R.id.searchHistoryRecycler)
        recyclerViewSearchHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackAdapterSearchHistory.trackList = searchHistoryTrackList
        getSearchHistory()
        recyclerViewSearchHistory.adapter = trackAdapterSearchHistory

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                editTextValue = s.toString()
                 if (inputEditText.hasFocus() && s?.isEmpty() == true  && searchHistoryTrackList.isNotEmpty()) {
                     clearState()
                     getSearchHistory()
                     searchHistoryLayout.visibility = View.VISIBLE
                } else {
                     searchHistoryLayout.visibility = View.GONE
                }
                if (inputEditText.hasFocus() && s?.isEmpty() == false) {
                    searchDebounce()
                }
            }
            override fun afterTextChanged(s: Editable?) {
               editTextValue = s.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            searchHistoryLayout.visibility = if (hasFocus && inputEditText.text.isEmpty() && searchHistoryTrackList.isNotEmpty()) View.VISIBLE else View.GONE
            getSearchHistory()
        }

        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackAdapter.trackList = trackList
        recyclerView.adapter = trackAdapter

        //слушатель нажатия Enter на вирт клавиатуре
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editTextValue.isNotEmpty()) {
                    search()
                }
                if (editTextValue.isEmpty() and (trackList.isNotEmpty() or placeholderImage.isVisible)) {
                    clearState()
                }
            }
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //очищаем очередь при закрытии экрана
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //сохраняем введенное значение в EditText  при пересоздании экрана
        outState.putString(EDITTEXT_VALUE, editTextValue)

        /*
        убрала до следующих спринтов по рекомедации ревьюера
        //сохраняем список выведенных из запроса треков при пересоздании экрана
        val gson = Gson()
        val json = gson.toJson(trackList)
        outState.putString(TRACKLIST_TO_JSON, json)

        //сохраняем код ответа на запрос
        outState.putInt(RESPONSE_CODE, responseCode)
         */
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(EDITTEXT_VALUE, EDITTEXT_VALUE_DEF)

        /*
        убрала до следующих спринтов по рекомедации ревьюера
        val gson = Gson()
        val json = savedInstanceState.getString(TRACKLIST_TO_JSON, EDITTEXT_VALUE_DEF)
        val type = object : TypeToken<List<Track>>() {}.type
        trackList.clear()
        trackList.addAll(gson.fromJson(json, type))
        trackAdapter.notifyDataSetChanged()

        responseCode = savedInstanceState.getInt(RESPONSE_CODE)
        if (trackList.isEmpty() and editTextValue.isNotEmpty()) {
            processResponseCode(responseCode, trackList)
        }
         */
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun getSearchHistory() {
        searchHistoryInteractorImpl.getSearchHistory(
            consumer = object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(foundSearchHistoryTracks: List<Track>) {
                    searchHistoryTrackList.clear()
                    searchHistoryTrackList.addAll(foundSearchHistoryTracks)
                    trackAdapterSearchHistory.notifyDataSetChanged()
                }
            }
        )
    }

    private fun search(){
        if (inputEditText.text.toString().isEmpty())  {
            recyclerView.visibility = View.GONE
            return
        }

        // Меняем видимость элементов перед выполнением запроса
        clearState()

        progressBar.visibility = View.VISIBLE

        trackListInteractorImpl.search(
            expression = inputEditText.text.toString(),
            consumer = object : TrackListInteractor.TrackListConsumer {
                override fun consume(foundTracks: Resource<List<Track>>) {
                    handler.post {
                        progressBar.visibility = View.GONE
                        if (foundTracks is Resource.Error) {
                            placeholderImage.visibility = View.VISIBLE
                            placeholderMessage.visibility = View.VISIBLE
                            placeholderMessageDescription.visibility = View.VISIBLE
                            refreshButton.visibility = View.VISIBLE
                            placeholderImage.setImageResource(R.drawable.ic_network_error_120)
                            placeholderMessage.text = getString(R.string.something_went_wrong)
                            placeholderMessageDescription.text =
                                getString(R.string.something_went_wrong_description)
                        } else if (foundTracks is Resource.Success) {
                            if (foundTracks.data.isEmpty()) {
                                placeholderImage.visibility = View.VISIBLE
                                placeholderMessage.visibility = View.VISIBLE
                                placeholderMessage.text = getString(R.string.nothing_found)
                                placeholderImage.setImageResource(R.drawable.ic_error_nothing_found_120)
                            } else {
                                trackList.addAll(foundTracks.data)
                                trackAdapter.notifyDataSetChanged()
                                placeholderImage.visibility = View.GONE
                                placeholderMessage.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        )
    }

    private fun clearState(){
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        recyclerView.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderMessageDescription.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    companion object {
        private const val EDITTEXT_VALUE = "EDITTEXT_VALUE"
        private const val EDITTEXT_VALUE_DEF = ""
        private const val TRACKLIST_TO_JSON = "TRACKLIST_TO_JSON"
        private const val RESPONSE_CODE = "RESPONSE_CODE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }
}

