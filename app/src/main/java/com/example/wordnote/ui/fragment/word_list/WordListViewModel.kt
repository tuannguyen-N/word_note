package com.example.wordnote.ui.fragment.word_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.domain.LocalWordUseCase
import com.example.wordnote.util.SpeakingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WordListViewModel(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager
) : ViewModel() {
    val uiEvent = MutableSharedFlow<WordListUIEvent>()
    val wordList = MutableStateFlow<List<WordEntity>>()

    fun onAction(action: WordListAction) {
        when (action) {
            is WordListAction.OnOpenDetailWordDialog -> performOpenDetailWordDialog(action.word)
            is WordListAction.OnShowAddWordDialog -> performOpenAddWordDialog()
            is WordListAction.OnAddNewWord -> performAddNewWord(action.word)
            is WordListAction.OnSpeakingWord -> performSpeakingWord(action.word)
        }
    }

    private fun performSpeakingWord(word: String){
        speakingManager.speakingWord(word)
        sendUIEvent(WordListUIEvent.ShowToast(word))
    }

    private fun performAddNewWord(word: String) {
        val updated = wordList.value.toMutableList()
        val words = word.split(",").map { it.trim() }

        for (word in words)
            updated.add(WordEntity(word, "demo"))

        wordList.value = updated
    }

    private fun performOpenAddWordDialog() {
        sendUIEvent(WordListUIEvent.ShowAddWordDialog)
    }

    private fun performOpenDetailWordDialog(word: WordEntity) {
        sendUIEvent(WordListUIEvent.ShowDetailWordDialog(word))
    }

    fun sendUIEvent(event: WordListUIEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            uiEvent.emit(event)
        }
    }
}