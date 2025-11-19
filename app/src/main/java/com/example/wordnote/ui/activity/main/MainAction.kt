package com.example.wordnote.ui.activity.main

sealed interface MainAction {
    data class SendWordFromNotification(val word: String) : MainAction
}