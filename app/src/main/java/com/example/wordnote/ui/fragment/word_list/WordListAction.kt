package com.example.wordnote.ui.fragment.word_list

import com.example.wordnote.domain.model.WordData
import com.example.wordnote.util.SortType

sealed interface WordListAction {
    class OnOpenDetailWordDialog(val word: WordData) : WordListAction
    object OnShowAddWordDialog : WordListAction
    data class OnSpeakingWord(val word: String) : WordListAction
    data class OnSaveWord(val word: String, val level: Int) : WordListAction
    data class OnDeleteWord(val word: WordData): WordListAction
    data class OnUpdateLevel(val word: WordData): WordListAction
    data class OnSortWords(val sortType: SortType) : WordListAction
    data class OnUpdateNote(val word: WordData): WordListAction
}