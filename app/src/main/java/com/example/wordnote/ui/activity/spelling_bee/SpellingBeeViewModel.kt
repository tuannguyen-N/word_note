package com.example.wordnote.ui.activity.spelling_bee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.SpellingBeeState
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.manager.SpeakingManager
import com.example.wordnote.manager.SpellingBeeGameEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SpellingBeeViewModel(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager
) : ViewModel() {

    private val _state = MutableStateFlow(SpellingBeeState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SpellingBeeUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private lateinit var engine: SpellingBeeGameEngine

    fun onAction(action: SpellingBeeAction) = viewModelScope.launch {
        when (action) {
            is SpellingBeeAction.InitWord -> initGame(action.categoryId)
            is SpellingBeeAction.OnSubmit -> submit(action.input)
            is SpellingBeeAction.OnSpeakingCurrentWord -> speakCurrent()
            is SpellingBeeAction.OnShowAnswers -> showAnswers()
        }
    }

    private suspend fun initGame(categoryId: Int) {
        _state.update { it.copy(categoryId = categoryId, isBusy = true) }

        val words = localWordUseCase.getWordsByCategory(categoryId).first()
        engine = SpellingBeeGameEngine(words)

        val first = engine.nextWord()

        _state.update {
            it.copy(
                words = words,
                currentWord = first,
                isBusy = false
            )
        }

        first?.let { speak(it.word) }
    }

    private suspend fun submit(input: String) {
        val st = _state.value
        if (!st.isSubmitEnabled || st.isBusy) return

        if (engine.verify(input)) {
            _state.update { it.copy(isSubmitEnabled = false, isBusy = true) }
            _uiEvent.emit(SpellingBeeUIEvent.OnCorrect)

            delay(1000)
            moveToNextWord()
        } else {
            val incorrect = st.incorrectionCount + 1
            _uiEvent.emit(SpellingBeeUIEvent.OnInCorrect)

            _state.update {
                it.copy(
                    incorrectionCount = incorrect,
                    isShowAnswers = incorrect >= 3
                )
            }
        }
    }

    private suspend fun moveToNextWord() {
        val next = engine.nextWord()
        if (next == null) {
            finishGame()
        } else {
            _state.update {
                it.copy(
                    currentWord = next,
                    isSubmitEnabled = true,
                    isShowAnswers = false,
                    incorrectionCount = 0,
                    isBusy = false
                )
            }
            _uiEvent.emit(SpellingBeeUIEvent.OnNextWord)
            speak(next.word)
        }
    }

    private suspend fun showAnswers() {
        _state.update { it.copy(isBusy = true, isSubmitEnabled = false) }

        val current = _state.value.currentWord ?: return
        _uiEvent.emit(SpellingBeeUIEvent.ShowAnswersUI(current.word))

        delay(3000)
        moveToNextWord()
    }

    private fun speakCurrent() {
        state.value.currentWord?.let { speak(it.word) }
    }

    private fun speak(w: String) = speakingManager.speak(w)

    private suspend fun finishGame() {
        _state.update { it.copy(isFinished = true, isBusy = false) }
        _uiEvent.emit(SpellingBeeUIEvent.OnFinish)
    }
}

