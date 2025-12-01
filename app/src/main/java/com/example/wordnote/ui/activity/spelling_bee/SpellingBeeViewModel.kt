package com.example.wordnote.ui.activity.spelling_bee

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.SpellingBeeState
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.manager.SpeakingManager
import com.example.wordnote.manager.SpellingBeeGameEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpellingBeeViewModel(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<SpellingBeeUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private fun sendUIEvent(event: SpellingBeeUIEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }

    private val _categoryId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val wordsFlow = _categoryId
        .filterNotNull()
        .flatMapLatest { id -> localWordUseCase.getWordsByCategory(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _state = MutableStateFlow(SpellingBeeState())
    val state = combine(wordsFlow, _state) { words, state ->
        state.copy(words = words)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SpellingBeeState())

    private var engine: SpellingBeeGameEngine? = null

    fun onAction(action: SpellingBeeAction) {
        when (action) {
            is SpellingBeeAction.InitWord -> initGame(action.categoryId)
            is SpellingBeeAction.OnSubmit -> submitWord(action.input)
            is SpellingBeeAction.OnSpeakingCurrentWord -> performSpeakingCurrentWord()
        }
    }

    private fun performSpeakingCurrentWord() {
        state.value.currentWord?.let { speak(it.word) }
    }

    private fun initGame(categoryId: Int) {
        _categoryId.value = categoryId

        viewModelScope.launch {
            val list = wordsFlow.filter { it.isNotEmpty() }.first()
            engine = SpellingBeeGameEngine(list)

            val firstWord = engine?.nextWord()
            updateCurrentWord(firstWord)
        }
    }

    private fun submitWord(input: String) {
        val game = engine ?: return
        if (!_state.value.isSubmitEnabled) return

        if (game.verify(input)) {
            processCorrectAnswer(game)
        } else {
            processIncorrectAnswer()
        }
    }

    private fun processIncorrectAnswer() {
        sendUIEvent(SpellingBeeUIEvent.OnInCorrect)
    }

    private fun processCorrectAnswer(game: SpellingBeeGameEngine) =
        viewModelScope.launch {
            disableSubmitInteraction()

            sendUIEvent(SpellingBeeUIEvent.OnCorrect)
            delay(1000)
            sendUIEvent(SpellingBeeUIEvent.OnNextWord)

            val next = game.nextWord()
            if (next == null) finishGame()
            else updateCurrentWord(next)
            enableSubmitInteraction()
        }


    private fun disableSubmitInteraction() {
        _state.update { it.copy(isSubmitEnabled = false) }
    }

    private fun enableSubmitInteraction() {
        _state.update { it.copy(isSubmitEnabled = true) }
    }

    private fun updateCurrentWord(word: WordData?) {
        _state.update {
            it.copy(currentWord = word)
        }
        word ?: return
        speak(word.word)
    }

    private fun finishGame() {
        sendUIEvent(SpellingBeeUIEvent.OnFinish)
    }

    private fun speak(word: String) {
        speakingManager.speak(word)
    }
}
