package com.practicum.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = ""
    private var trackList: MutableList<Track> = mutableListOf()
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private lateinit var inputEditText: EditText
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderMessageDescription: TextView
    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private var responseCode: Int = 0
    
    private val trackClickListener = TrackAdapter.TrackClickListener {
        // обработка нажатия на трек - добавление в историю поиска
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)
        searchHistory.addTrackToSearchHistory(it)
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

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }
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

         val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

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
        val searchHistory = SearchHistory(sharedPrefs)
        val searchHistoryLayout = findViewById<ViewGroup>(R.id.search_history_layout)
        val clearHistoryButton = findViewById<Button>(R.id.button_clear_search_history)
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            trackAdapterSearchHistory.notifyDataSetChanged()
            searchHistoryLayout.visibility = View.GONE
        }
        val recyclerViewSearchHistory = findViewById<RecyclerView>(R.id.searchHistoryRecycler)
        recyclerViewSearchHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackAdapterSearchHistory.trackList = searchHistory.trackList
        recyclerViewSearchHistory.adapter = trackAdapterSearchHistory

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                editTextValue = s.toString()
                 if (inputEditText.hasFocus() && s?.isEmpty() == true  && searchHistory.trackList.isNotEmpty()) {
                     searchHistoryLayout.visibility = View.VISIBLE
                     searchHistory.getSearchHistory()
                     trackAdapterSearchHistory.notifyDataSetChanged()
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
            searchHistoryLayout.visibility = if (hasFocus && inputEditText.text.isEmpty() && searchHistory.trackList.isNotEmpty()) View.VISIBLE else View.GONE
            searchHistory.getSearchHistory()
            trackAdapterSearchHistory.notifyDataSetChanged()
        }

        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        trackAdapter.trackList = trackList
        recyclerView.adapter = trackAdapter

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

    companion object {
        private const val EDITTEXT_VALUE = "EDITTEXT_VALUE"
        private const val EDITTEXT_VALUE_DEF = ""
        private const val TRACKLIST_TO_JSON = "TRACKLIST_TO_JSON"
        private const val RESPONSE_CODE = "RESPONSE_CODE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search() {
        // Меняем видимость элементов перед выполнением запроса
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderMessageDescription.visibility = View.GONE
        refreshButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackListResponse> {
                override fun onResponse(
                    call: Call<TrackListResponse>,
                    response: Response<TrackListResponse>
                ) {
                    progressBar.visibility = View.GONE // Прячем ProgressBar после успешного выполнения запроса
                    recyclerView.visibility = View.VISIBLE
                    responseCode = response.code()
                    processResponseCode(responseCode, response.body()?.results)
                }

                override fun onFailure(call: Call<TrackListResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE // Прячем ProgressBar после выполнения запроса с ошибкой
                    responseCode = 500
                    processResponseCode(responseCode,null)
                }

            })
    }

    private fun processResponseCode(responseCode: Int, responseList: MutableList<Track>?) {
        when (responseCode) {
            200 -> {
                trackList.clear()
                if (responseList?.isNotEmpty() == true) {
                    trackList.addAll(responseList)
                    placeholderImage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE

                } else {
                    placeholderImage.visibility = View.VISIBLE
                    placeholderMessage.visibility = View.VISIBLE
                    placeholderMessage.text = getString(R.string.nothing_found)
                    placeholderImage.setImageResource(R.drawable.ic_error_nothing_found_120)
                }
                trackAdapter.notifyDataSetChanged()
                placeholderMessageDescription.visibility = View.GONE
                refreshButton.visibility = View.GONE
            }
            else -> {
                    placeholderImage.visibility = View.VISIBLE
                    placeholderMessage.visibility = View.VISIBLE
                    placeholderMessageDescription.visibility = View.VISIBLE
                    refreshButton.visibility = View.VISIBLE
                    placeholderImage.setImageResource(R.drawable.ic_network_error_120)
                    placeholderMessage.text = getString(R.string.something_went_wrong)
                    placeholderMessageDescription.text =
                        getString(R.string.something_went_wrong_description)
            }
        }
    }

    private fun clearState(){
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderImage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderMessageDescription.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }
}

