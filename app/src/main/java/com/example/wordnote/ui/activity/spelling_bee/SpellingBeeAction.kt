package com.example.wordnote.ui.activity.spelling_bee

sealed interface SpellingBeeAction {
    data class InitWord(val categoryId: Int) : SpellingBeeAction
    data class OnSubmit(val input: String): SpellingBeeAction
    data class PlayOnlyWord(val wordId: Int): SpellingBeeAction
    object OnSpeakingCurrentWord: SpellingBeeAction
    object OnShowAnswers: SpellingBeeAction
    object OnPlayAgain: SpellingBeeAction
}