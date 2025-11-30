package com.example.wordnote.ui.activity.word

import com.example.wordnote.domain.model.WordData
import com.example.wordnote.utils.SortType

sealed interface WordAction {
    class OnOpenDetailWordDialog(val word: WordData) : WordAction
    object OnShowAddWordDialog : WordAction
    data class OnSpeakingWord(val word: String) : WordAction
    data class OnSaveWord(val word: String) : WordAction
    data class OnDeleteWord(val word: WordData): WordAction
    data class OnUpdateLevel(val word: WordData): WordAction
    data class OnSortWords(val sortType: SortType) : WordAction
    data class OnUpdateNote(val word: WordData): WordAction
    data class OnStartStudying(val word: WordData): WordAction
    data class OnStopStudying(val wordId: Int): WordAction
    data class OnDeleteWords(val words: Set<Int>): WordAction
    data class OnSearchWord(val query: String): WordAction
}