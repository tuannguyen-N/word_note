package com.example.wordnote.ui.activity.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.WordState
import com.example.wordnote.domain.usecase.ScheduleWordUseCase
import com.example.wordnote.util.Result
import com.example.wordnote.util.SortType
import com.example.wordnote.util.SpeakingManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class WordViewModel(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager,
    private val scheduleWordUseCase: ScheduleWordUseCase
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<WordUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _categoryId = MutableStateFlow<Int?>(null)
    private val _sortType = MutableStateFlow<SortType>(SortType.WORD)
    private val _words =
        combine(_sortType, _categoryId) { sortType, categoryId -> Pair(sortType, categoryId) }
            .flatMapLatest { (sortType, categoryId) ->
                if (categoryId != null && categoryId != -1) {
                    when (sortType) {
                        is SortType.WORD -> localWordUseCase.getWordsByCategory(categoryId)
                        is SortType.LEVEL -> localWordUseCase.getWordsByCategoryAndLevel(
                            categoryId,
                            sortType.level
                        )
                    }
                } else {
                    when (sortType) {
                        is SortType.WORD -> localWordUseCase.getWordsOrderedByWord()
                        is SortType.LEVEL -> localWordUseCase.getWordsByLevel(sortType.level)
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(WordState(isLoading = true))
    val state = combine(_state, _sortType, _words) { state, sortType, words ->
        state.copy(
            words = words, sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WordState())

    init {
        performShowLoading(true)
        viewModelScope.launch {
            _words.first()
            performShowLoading(false)
        }
    }

    fun setCategoryId(id: Int) {
        _categoryId.value = id
    }

    fun onAction(action: WordAction) {
        when (action) {
            is WordAction.OnOpenDetailWordDialog -> performOpenDetailWordDialog(action.word)

            is WordAction.OnShowAddWordDialog -> performOpenAddWordDialog()

            is WordAction.OnSpeakingWord -> performSpeakingWord(action.word)

            is WordAction.OnSaveWord -> performSaveWord(action.word, action.level)

            is WordAction.OnSortWords -> performSortWords(action.sortType)

            is WordAction.OnDeleteWord -> performDeleteWord(action.word)

            is WordAction.OnUpdateLevel -> performUpdateLevel(action.word)

            is WordAction.OnUpdateNote -> performUpdateNote(action.word)
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
        val newLevel = when (sortType) {
            is SortType.LEVEL -> if (_state.value.selectedLevel == sortType.level) null else sortType.level
            is SortType.WORD -> null
        }
        val newSort = newLevel?.let { SortType.LEVEL(it) } ?: SortType.WORD
        _sortType.value = newSort
        _state.update { it.copy(selectedLevel = newLevel) }
        sendUIEvent(WordUIEvent.HideLevelContainer)
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
        val categoryId = _categoryId.value
        if (categoryId == null) {
            sendUIEvent(WordUIEvent.ShowToast("Missing category"))
            return
        }
        when (val result = localWordUseCase.upsertWord(word, level, categoryId)) {
            is Result.Error -> sendUIEvent(WordUIEvent.ShowToast("Can't save word: $word"))
            is Result.NotFound -> sendUIEvent(WordUIEvent.ShowToast("Not found: $word"))
            is Result.Success -> {
                if (result.word?.level != 0) scheduleWordUseCase.scheduleWord(result.word!!)
            }

            is Result.AlreadyExists -> sendUIEvent(WordUIEvent.ScrollToExistWord(word))
            is Result.AlreadyExistsInCategories ->
                sendUIEvent(WordUIEvent.ShowToast("Already exists in: ${result.categoryNames}"))
        }
    }

    private fun performShowLoading(shouldShowLoading: Boolean) {
        _state.update { it.copy(isLoading = shouldShowLoading) }
    }

    private fun performOpenAddWordDialog() {
        sendUIEvent(WordUIEvent.ShowAddWordDialog)
    }

    private fun performOpenDetailWordDialog(word: WordData) {
        sendUIEvent(WordUIEvent.ShowDetailWordDialog(word))
    }

    fun sendUIEvent(event: WordUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}