package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale


class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = ""
    private var trackList = ArrayList<Track>()
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
    private var trackAdapter = TrackAdapter()
    private var responseCode: Int = 0

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

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                editTextValue = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
               editTextValue = s.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
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
        //сохраняем список выведенных из запроса треков при пересоздании экрана
        val gson = Gson()
        val json = gson.toJson(trackList)
        outState.putString(TRACKLIST_TO_JSON, json)
        //сохраняем код ответа на запрос
        outState.putInt(RESPONSE_CODE, responseCode)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(EDITTEXT_VALUE, EDITTEXT_VALUE_DEF)

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
    }

    companion object {
        private const val EDITTEXT_VALUE = "EDITTEXT_VALUE"
        private const val EDITTEXT_VALUE_DEF = ""
        private const val TRACKLIST_TO_JSON = "TRACKLIST_TO_JSON"
        private const val RESPONSE_CODE = "RESPONSE_CODE"
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search() {
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TrackListResponse> {
                override fun onResponse(
                    call: Call<TrackListResponse>,
                    response: Response<TrackListResponse>
                ) {
                    responseCode = response.code()
                    processResponseCode(responseCode, response.body()?.results)
                }

                override fun onFailure(call: Call<TrackListResponse>, t: Throwable) {
                    responseCode = 500
                    processResponseCode(responseCode,null)
                }

            })
    }

    private fun processResponseCode(responseCode: Int, responseList: ArrayList<Track>?) {
        when (responseCode) {
            200 -> {
                trackList.clear()
                if (responseList?.isNotEmpty() == true) {
                    trackList.addAll(responseList)
                    trackList.forEach { it ->
                        val trackTimeNotNull = it.trackTime ?: "00"
                        it.trackTime = SimpleDateFormat(
                            "mm:ss",
                            Locale.getDefault()
                        ).format(trackTimeNotNull.toLong())
                    }
                    placeholderImage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                } else {
                    placeholderImage.visibility = View.VISIBLE
                    placeholderMessage.visibility = View.VISIBLE
                    placeholderMessage.text = getString(R.string.nothing_found)
                    placeholderImage.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_error_nothing_found_120, null)
                    )
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

                    placeholderImage.setImageDrawable(
                        resources.getDrawable(R.drawable.ic_network_error_120, null)
                    )
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

