package com.example.wordnote.ui.fragment.word_list

import com.example.wordnote.data.entities.WordEntity

sealed interface WordListAction {
    class OnOpenDetailWordDialog(val word: WordEntity): WordListAction
    object OnShowAddWordDialog : WordListAction
    class OnAddNewWord(val word: String) : WordListAction
    class OnSpeakingWord(val word:String): WordListAction
    class SaveWord(val word: WordEntity): WordListAction
}