package com.example.wordnote.ui.fragment.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.state.FocusState
import com.example.wordnote.manager.TimerHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FocusViewModel : ViewModel() {
    private val _uiEvent = MutableSharedFlow<FocusUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _timeValue = MutableStateFlow(1f)
    val timeValue = _timeValue.asStateFlow()

    private val _state = MutableStateFlow(FocusState())
    val state = _state.asStateFlow()

    private val timerHelper = TimerHelper(viewModelScope)

    fun onAction(action: FocusAction) {
        when (action) {
            is FocusAction.StartPauseFocus -> performPlayPause()
            is FocusAction.OnStopFocus -> performStopFocus()
            is FocusAction.OnPauseTime -> performStopTime()
            is FocusAction.OnResumeTime -> performOnResumeTime()
            is FocusAction.OnChangeTime -> performOnChangeTime(action.value)
        }
    }

    private fun performOnChangeTime(value: Float) {
        _timeValue.value = value

        val totalMinutes = 15 + (value - 1f) * 5
        val totalSeconds = (totalMinutes * 60).toLong()

        _state.update {
            it.copy(
                totalSeconds = totalSeconds,
                remainingSeconds = totalSeconds,
                isStartFocussing = false,
                isRunning = false
            )
        }
    }


    private fun performOnResumeTime() {
        resume()
    }

    private fun performStopTime() {
        pause()
    }

    private fun performStopFocus() {
        reset()
        _state.update { it.copy(isStartFocussing = false) }
    }

    private fun performPlayPause() {
        val s = _state.value
        when {
            s.isRunning -> pause()
            s.remainingSeconds <= 0 -> {
                reset()
                start()
            }

            s.remainingSeconds < s.totalSeconds -> resume()
            else -> start()
        }
    }

    private fun start() {
        timerHelper.start(
            totalSeconds = _state.value.totalSeconds,
            onTick = { sec ->
                _state.update {
                    it.copy(
                        remainingSeconds = sec,
                        isRunning = true,
                        isStartFocussing = true
                    )
                }
            },
            onFinish = { _state.update { it.copy(isRunning = false) } }
        )
    }

    private fun pause() {
        timerHelper.pause()
        _state.update { it.copy(isRunning = false) }
    }

    private fun resume() {
        timerHelper.resume(
            onTick = { sec -> _state.update { it.copy(remainingSeconds = sec, isRunning = true) } },
            onFinish = { _state.update { it.copy(isRunning = false) } }
        )
    }

    private fun reset() {
        timerHelper.reset()
        _state.update { it.copy(remainingSeconds = _state.value.totalSeconds, isRunning = false) }
    }

    private fun sendUIEvent(event: FocusUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
