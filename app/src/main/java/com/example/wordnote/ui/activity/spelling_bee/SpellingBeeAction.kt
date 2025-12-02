package com.example.wordnote.ui.activity.spelling_bee

sealed interface SpellingBeeAction {
    data class InitWord(val categoryId: Int) : SpellingBeeAction
    data class OnSubmit(val input: String): SpellingBeeAction
    object OnSpeakingCurrentWord: SpellingBeeAction
    object OnShowAnswers: SpellingBeeAction
}