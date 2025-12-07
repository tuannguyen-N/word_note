package com.example.wordnote.ui.activity.word

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.state.WordState
import com.example.wordnote.domain.model.Result
import com.example.wordnote.domain.model.SortType
import com.example.wordnote.manager.SpeakingManager
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

    private val _searchQuery = MutableStateFlow("")
    private val _filterWords = combine(_words,_searchQuery){words, query->
        if (query.isBlank()) words
        else words.filter { it.word.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(WordState(isLoading = true))
    val state = combine(_state, _sortType, _filterWords) { state, sortType, filterWords ->
        state.copy(
            words = filterWords, sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WordState())

    init {
        performShowLoading(true)
        viewModelScope.launch {
            _words.first()
            performShowLoading(false)
        }
    }

    fun onAction(action: WordAction) {
        when (action) {
            is WordAction.OnOpenDetailWordDialog -> performOpenDetailWordDialog(action.word)

            is WordAction.OnShowAddWordDialog -> performOpenAddWordDialog()

            is WordAction.OnSpeakingWord -> performSpeakingWord(action.word)

            is WordAction.OnSaveWord -> performSaveWord(action.word)

            is WordAction.OnSortWords -> performSortWords(action.sortType)

            is WordAction.OnDeleteWord -> performDeleteWord(action.wordId)

            is WordAction.OnUpdateLevel -> performUpdateLevel(action.word)

            is WordAction.OnUpdateNote -> performUpdateNote(action.word)

            is WordAction.OnStartStudying -> performStartStudying(action.word)

            is WordAction.OnStopStudying -> performStopStudying(action.wordId)

            is WordAction.OnSearchWord -> performSearchWord(action.query)

            is WordAction.InitCategory -> performInitCategory(action.id)
        }
    }

    private fun performInitCategory(id: Int){
        _categoryId.value = id
    }

    private fun performSearchWord(query: String){
        _searchQuery.value = query
    }

    private fun performStopStudying(wordId: Int) {
        viewModelScope.launch {
            localWordUseCase.stopStudying(wordId)
        }
    }

    private fun performStartStudying(word: WordData) {
        viewModelScope.launch {
            Log.e("ádfasdfasdf", "performStartStudying: ${localWordUseCase.countStudyingWords()}")
            if (localWordUseCase.countStudyingWords() >= AppPreferences.maxWords)
                sendUIEvent(WordUIEvent.ShowFullStudyingWords)
            else
                localWordUseCase.startStudying(word)
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
//            scheduleWordUseCase.scheduleWord(word)
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

    private fun performDeleteWord(wordId: Int) {
        viewModelScope.launch {
            localWordUseCase.deleteWord(wordId)
        }
    }

    private fun performSpeakingWord(word: String) {
        speakingManager.speak(word)
    }

    private fun performSaveWord(word: String) {
        val words = word.split(",").map { it.trim() }.filter { it.isNotBlank() }
        viewModelScope.launch {
            performShowLoading(true)
            words.forEach {
                upsertWord(it)
            }
            performShowLoading(false)
        }
    }

    private suspend fun upsertWord(word: String) {
        val categoryId = _categoryId.value
        if (categoryId == null) {
            sendUIEvent(WordUIEvent.ShowToast("Missing category"))
            return
        }
        when (val result = localWordUseCase.upsertWord(word, categoryId)) {
            is Result.Error -> sendUIEvent(WordUIEvent.ShowToast("Can't save word: $word"))
            is Result.NotFound -> sendUIEvent(WordUIEvent.ShowToast("Not found: $word"))
            is Result.Success -> {
//                if (result.word?.level != 0) scheduleWordUseCase.scheduleWord(result.word!!)
            }
            is Result.AlreadyExists -> sendUIEvent(WordUIEvent.ScrollToExistWord(word))
            is Result.AlreadyExistsInCategories ->
                sendUIEvent(WordUIEvent.ShowExistWordDialog(result.category))
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

    private fun sendUIEvent(event: WordUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}