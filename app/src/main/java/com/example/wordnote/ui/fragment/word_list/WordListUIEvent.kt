package com.example.wordnote.ui.fragment.word_list

import com.example.wordnote.data.entities.WordEntity

sealed interface WordListUIEvent {
    class ShowDetailWordDialog(val word: WordEntity): WordListUIEvent
    object ShowAddWordDialog: WordListUIEvent
    class ShowToast(val message: String): WordListUIEvent
}