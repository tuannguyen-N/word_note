package com.example.wordnote.ui.activity.main

sealed interface MainAction {
    data class OnChangeDeleteMode(val isDeleteMode: Boolean): MainAction
    object RequestDelete: MainAction
}