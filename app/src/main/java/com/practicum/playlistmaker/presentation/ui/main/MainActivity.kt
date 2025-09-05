package com.practicum.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.library.LibraryActivity
import com.practicum.playlistmaker.presentation.ui.search.SearchActivity
import com.practicum.playlistmaker.presentation.ui.settings.SettingsActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.search_button)
        buttonSearch.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        val buttonLibrary = findViewById<Button>(R.id.library_button)
        buttonLibrary.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        val buttonSettings = findViewById<Button>(R.id.settings_button)
        buttonSettings.setOnClickListener {
            val buttonIntent = Intent(this, SettingsActivity::class.java)
            startActivity(buttonIntent)
        }
    }
}