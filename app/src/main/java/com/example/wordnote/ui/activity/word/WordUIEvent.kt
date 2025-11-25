package com.example.wordnote.ui.activity.word

import com.example.wordnote.domain.model.WordData

sealed interface WordUIEvent {
    class ShowDetailWordDialog(val word: WordData) : WordUIEvent
    object ShowAddWordDialog : WordUIEvent
    class ShowToast(val message: String) : WordUIEvent
    object HideLevelContainer: WordUIEvent
    data class ScrollToExistWord(val word: String): WordUIEvent
    object ShowFullStudyingWords: WordUIEvent
}