package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.util.getDateFormat

sealed class PlayerState( val progress: String) {

    class Default : PlayerState(getDateFormat(null))

    class Prepared : PlayerState(getDateFormat(null))

    class Playing(progress: String) : PlayerState(progress)

    class Paused(progress: String) : PlayerState(progress)
}