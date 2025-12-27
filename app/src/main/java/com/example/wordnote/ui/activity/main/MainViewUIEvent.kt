package com.example.wordnote.ui.activity.main

sealed interface MainViewUIEvent {
    data class ChangeDeleteMode(val isDeleteMode: Boolean) : MainViewUIEvent
    object RequestDelete: MainViewUIEvent
}