package com.practicum.playlistmaker.sharing

sealed interface SharingState {

    object ShareApp : SharingState
    object Agreement : SharingState
    object SendEmail : SharingState

}