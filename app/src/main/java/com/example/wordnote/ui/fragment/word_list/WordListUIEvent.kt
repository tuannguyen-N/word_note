package com.example.wordnote.ui.fragment.word_list

import com.example.wordnote.domain.model.WordData

sealed interface WordListUIEvent {
    class ShowDetailWordDialog(val word: WordData) : WordListUIEvent
    object ShowAddWordDialog : WordListUIEvent
    class ShowToast(val message: String) : WordListUIEvent
    object HideLevelContainer: WordListUIEvent
    data class ScrollToExistWord(val word: String): WordListUIEvent
}