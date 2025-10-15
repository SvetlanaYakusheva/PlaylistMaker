package com.practicum.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding


class LibraryActivity : AppCompatActivity() {
    private lateinit var tabMediator: TabLayoutMediator
    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButtonLibrary.setOnClickListener {
            finish()
        }

        binding.viewPagerLibrary.adapter = LibraryAdapter(
            supportFragmentManager,
            lifecycle,
            )

        tabMediator = TabLayoutMediator(binding.tabLayoutLibrary, binding.viewPagerLibrary) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }



    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}