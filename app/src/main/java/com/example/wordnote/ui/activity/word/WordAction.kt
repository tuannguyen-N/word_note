package com.example.wordnote.ui.activity.word

import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.SortType

sealed interface WordAction {
    object OnShowAddWordDialog : WordAction
    data class OnOpenDetailWordDialog(val word: WordData) : WordAction
    data class OnSpeakingWord(val word: String) : WordAction
    data class OnSaveWord(val word: String) : WordAction
    data class OnDeleteWord(val wordId: Int): WordAction
    data class OnUpdateLevel(val word: WordData): WordAction
    data class OnSortWords(val sortType: SortType) : WordAction
    data class OnUpdateNote(val word: WordData): WordAction
    data class OnStartStudying(val word: WordData): WordAction
    data class OnStopStudying(val wordId: Int): WordAction
    data class OnSearchWord(val query: String): WordAction
    data class InitCategory(val id: Int): WordAction
}