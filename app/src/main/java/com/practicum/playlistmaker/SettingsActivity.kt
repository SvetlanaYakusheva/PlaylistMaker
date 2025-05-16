package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBackToMainActivity = findViewById<ImageButton>(R.id.back_button)
        buttonBackToMainActivity.setOnClickListener {
            val backToMainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainActivityIntent)
        }
    }
}