package com.example.wordnote.ui.activity.main

sealed interface MainViewUIEvent {
    data class SendWordFromNotification(val word: String) : MainViewUIEvent
}