package com.example.wordnote.ui.activity.spelling_bee

sealed interface SpellingBeeUIEvent {
    object OnCorrect: SpellingBeeUIEvent
    object OnInCorrect: SpellingBeeUIEvent
    object OnNextWord: SpellingBeeUIEvent
    object OnFinish: SpellingBeeUIEvent
}