package com.example.wordnote.ui.activity.spelling_bee

sealed interface SpellingBeeUIEvent {
    object OnNextWord : SpellingBeeUIEvent
    object OnFinish : SpellingBeeUIEvent
    data class ShowAnswersUI(val answer: String) : SpellingBeeUIEvent
}