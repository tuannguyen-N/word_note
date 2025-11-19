package com.example.wordnote.ui.fragment.word_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.data.api.RetrofitInstance
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.WordState
import com.example.wordnote.domain.usecase.ScheduleWordUseCase
import com.example.wordnote.util.Result
import com.example.wordnote.util.SortType
import com.example.wordnote.util.SpeakingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class WordListViewModel(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager,
    private val scheduleWordUseCase: ScheduleWordUseCase
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<WordListUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _sortType = MutableStateFlow<SortType>(SortType.WORD)
    private val _words = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            is SortType.WORD -> localWordUseCase.getWordsOrderedByWord()
            is SortType.LEVEL -> localWordUseCase.getWordsByLevel(sortType.level)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(WordState(isLoading = true))
    val state = combine(_state, _sortType, _words) { state, sortType, words ->
        state.copy(
            words = words, sortType = sortType, isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WordState())

    fun onAction(action: WordListAction) {
        when (action) {
            is WordListAction.OnOpenDetailWordDialog -> performOpenDetailWordDialog(action.word)

            is WordListAction.OnShowAddWordDialog -> performOpenAddWordDialog()

            is WordListAction.OnSpeakingWord -> performSpeakingWord(action.word)

            is WordListAction.OnSaveWord -> performSaveWord(action.word, action.level)

            is WordListAction.OnSortWords -> performSortWords(action.sortType)

            is WordListAction.OnDeleteWord -> performDeleteWord(action.word)

            is WordListAction.OnUpdateLevel -> performUpdateLevel(action.word)

            is WordListAction.OnUpdateNote -> performUpdateNote(action.word)
        }
    }

    private fun performUpdateNote(word: WordData) {
        viewModelScope.launch {
            localWordUseCase.updateNote(word)
        }
    }

    private fun performUpdateLevel(word: WordData) {
        viewModelScope.launch {
            localWordUseCase.updateLevel(word)
            scheduleWordUseCase.scheduleWord(word)
        }
    }

    private fun performSortWords(sortType: SortType) {
        _state.update { state ->
            val newLevel = when (sortType) {
                is SortType.LEVEL -> if (state.selectedLevel == sortType.level) null else sortType.level
                is SortType.WORD -> null
            }
            val newSortType =
                newLevel?.let { SortType.LEVEL(it) } ?: SortType.WORD
            _sortType.value = newSortType
            state.copy(selectedLevel = newLevel)
        }
        sendUIEvent(WordListUIEvent.HideLevelContainer)
    }

    private fun performDeleteWord(word: WordData) {
        viewModelScope.launch {
            localWordUseCase.deleteWord(word)
        }
    }

    private fun performSpeakingWord(word: String) {
        speakingManager.speak(word)
    }

    private fun performSaveWord(word: String, level: Int) {
        val words = word.split(",").map { it.trim() }.filter { it.isNotBlank() }
        viewModelScope.launch {
            performShowLoading(true)
            words.forEach {
                upsertWord(it, level)
            }
            performShowLoading(false)
        }
    }

    private suspend fun upsertWord(word: String, level: Int) {
        when (val result = localWordUseCase.upsertWord(word, level)) {
            is Result.Error -> sendUIEvent(WordListUIEvent.ShowToast("Can't save word: $word"))
            is Result.NotFound -> sendUIEvent(WordListUIEvent.ShowToast("Not found: $word"))
            is Result.Success -> {
                scheduleWordUseCase.scheduleWord(result.word)
            }

            is Result.AlreadyExists -> sendUIEvent(WordListUIEvent.ScrollToExistWord(word))
        }
    }

    private fun performShowLoading(shouldShowLoading: Boolean) {
        _state.update { it.copy(isLoading = shouldShowLoading) }
    }

    private fun performOpenAddWordDialog() {
        sendUIEvent(WordListUIEvent.ShowAddWordDialog)
    }

    private fun performOpenDetailWordDialog(word: WordData) {
        sendUIEvent(WordListUIEvent.ShowDetailWordDialog(word))
    }

    fun sendUIEvent(event: WordListUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}