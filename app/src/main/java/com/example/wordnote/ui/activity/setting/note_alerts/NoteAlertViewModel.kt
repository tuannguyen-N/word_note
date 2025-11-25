package com.example.wordnote.ui.activity.setting.note_alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.domain.usecase.NoteAlertSettingUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NoteAlertViewModel(
    private val noteAlertSettingUseCase: NoteAlertSettingUseCase,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<NoteAlertSettingUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun onAction(action: NoteAlertSettingAction) {
        when (action) {
            is NoteAlertSettingAction.SetNotificationPost -> {
                performSetNotificationPost(action.canShow)
            }

            is NoteAlertSettingAction.SetMaxLearningWords -> {
                checkAndApplyMaxWords(action.maxWords)
            }

            is NoteAlertSettingAction.StartStudying -> {
                performStartStudying(action.id)
            }

            is NoteAlertSettingAction.StopStudying -> {
                performStopStudying(action.id)
            }
        }
    }

    private fun performStartStudying(id: Int){
        viewModelScope.launch {
            noteAlertSettingUseCase.startStudying(id)
        }
    }

    private fun performStopStudying(id: Int){
        viewModelScope.launch {
            noteAlertSettingUseCase.stopStudying(id)
        }
    }

    private fun checkAndApplyMaxWords(maxWords: Int) {
        viewModelScope.launch {
            val currentMax = AppPreferences.maxWords
            val studyingCount = noteAlertSettingUseCase.countStudyingWord()

            if (studyingCount <= maxWords) {
                val isDecrease = maxWords < currentMax
                AppPreferences.maxWords = maxWords

                if (isDecrease && maxWords <= 10) {
                    sendUIEvent(NoteAlertSettingUIEvent.ShowDialogMeme)
                }
            } else {
                sendUIEvent(NoteAlertSettingUIEvent.ResetSeekBar(currentMax.toFloat() / 5))
                sendUIEvent(NoteAlertSettingUIEvent.ShowDialogWordAvailable(noteAlertSettingUseCase.getWordByStudiedTime()))
            }
        }
    }

    private fun performSetNotificationPost(canShow: Boolean) {
        AppPreferences.canPostNotifications = canShow
        if (!canShow) {
            saveRemainingTime()
        } else
            restoreRemainingAlarms()
    }

    private fun saveRemainingTime() {
        viewModelScope.launch {
            val words = noteAlertSettingUseCase.getWordByStudiedTime()

            words.forEach { word ->
                noteAlertSettingUseCase.updateRemainingTime(word)
                noteAlertSettingUseCase.stopAlarm(word.id!!)
            }
        }
    }

    private fun restoreRemainingAlarms() {
        viewModelScope.launch {
            val words = noteAlertSettingUseCase.getAllWords()
            words.forEach { word ->
                noteAlertSettingUseCase.updateNextTrigger(word)
            }
        }
    }

    fun sendUIEvent(event: NoteAlertSettingUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}