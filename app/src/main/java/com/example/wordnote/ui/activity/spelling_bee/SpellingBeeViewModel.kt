package com.example.wordnote.ui.activity.spelling_bee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.SpellingInputState
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.state.SpellingBeeState
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

    private val failedWordIds = mutableSetOf<Int>()

    fun onAction(action: SpellingBeeAction) = viewModelScope.launch {
        when (action) {
            is SpellingBeeAction.InitWord -> initGame(action.categoryId)
            is SpellingBeeAction.OnSubmit -> submit(action.input)
            is SpellingBeeAction.OnSpeakingCurrentWord -> speakCurrent()
            is SpellingBeeAction.OnShowAnswers -> showAnswers()
            is SpellingBeeAction.OnPlayAgain -> resetGame()
            is SpellingBeeAction.PlayOnlyWord -> playOnlyWord(action.wordId)
        }
    }

    /**------------------- INIT -------------------------*/

    private fun startGame(
        words: List<WordData>,
        isSingleMode: Boolean
    ) {
        setState { copy(isBusy = true) }

        if (!state.value.isSingleMode)
            failedWordIds.clear()

        engine = SpellingBeeGameEngine(words)
        val first = engine.next()

        setState {
            copy(
                currentWord = first,
                totalCount = words.size,
                remainingCount = words.size,
                isBusy = false,
                isSubmitEnabled = true,
                incorrectionCount = 0,
                isShowAnswers = false,
                isFinished = false,
                isSingleMode = isSingleMode
            )
        }

        first?.let { speak(it.word) }
    }

    private suspend fun initGame(categoryId: Int) {
        setState { copy(categoryId = categoryId) }
        val words = localWordUseCase.getWordsByCategory(categoryId).first()
        startGame(words, isSingleMode = false)
    }

    private fun playOnlyWord(wordId: Int) = viewModelScope.launch {
        val word = localWordUseCase.getWordById(wordId)
        startGame(listOf(word), isSingleMode = true)
    }

    private suspend fun resetGame() {
        val category = state.value.categoryId ?: return
        setState { SpellingBeeState(categoryId = category, isBusy = true) }
        initGame(category)
        sendUIEvent(SpellingBeeUIEvent.OnNextWord)
    }

    /**------------------- SUBMIT -------------------------*/
    private suspend fun submit(input: String) {
        val st = state.value
        if (!st.isSubmitEnabled || st.isBusy) return

        val correct = engine.verify(input)

        if (correct) {
            onCorrect()
        } else {
            onIncorrect()
        }
    }

    private suspend fun onCorrect() {
        updateScore()

        setState {
            copy(
                inputState = SpellingInputState.CORRECT,
                isSubmitEnabled = false,
                isBusy = true
            )
        }

        delay(1000)

        if (state.value.isSingleMode) {
            finishGame()
        } else {
            moveToNextWord()
        }
    }

    private suspend fun updateScore() {
        val word = state.value.currentWord ?: return

        if (word.id !in failedWordIds) {
            localWordUseCase.updateScore(
                word.id!!,
                word.score + 1
            )
        }
    }

    private fun onIncorrect() {
        val wordId = state.value.currentWord?.id
        wordId?.let { failedWordIds.add(it) }

        val next = state.value.incorrectionCount + 1
        setState {
            copy(
                incorrectionCount = next,
                inputState = SpellingInputState.INCORRECT,
                isShowAnswers = next >= 3
            )
        }
    }

    /**------------------- NEXT WORD -------------------------*/
    private fun moveToNextWord() {
        if (engine.isFinished()) {
            finishGame()
            return
        }

        val next = engine.next()
        sendUIEvent(SpellingBeeUIEvent.OnNextWord)
        setState {
            copy(
                currentWord = next,
                incorrectionCount = 0,
                isShowAnswers = false,
                isSubmitEnabled = true,
                isBusy = false,
                inputState = SpellingInputState.NORMAL,
                remainingCount = remainingCount - 1
            )
        }
        speak(next?.word.orEmpty())
    }

    // ------------------- SHOW ANSWERS -------------------------
    private suspend fun showAnswers() {
        setState {
            copy(
                inputState = SpellingInputState.CORRECT,
                isSubmitEnabled = false,
                isBusy = true
            )
        }

        sendUIEvent(SpellingBeeUIEvent.ShowAnswersUI(state.value.currentWord?.word.orEmpty()))

        delay(3000)
        moveToNextWord()
    }


    private fun speakCurrent() = state.value.currentWord?.let { speak(it.word) }
    private fun speak(w: String) = speakingManager.speak(w)

    private fun finishGame() {
        setState { copy(isFinished = true, isBusy = false) }
        sendUIEvent(SpellingBeeUIEvent.OnFinish)
    }

    private fun setState(block: SpellingBeeState.() -> SpellingBeeState) {
        _state.update(block)
    }

    private fun sendUIEvent(e: SpellingBeeUIEvent) {
        viewModelScope.launch { _uiEvent.emit(e) }
    }
}