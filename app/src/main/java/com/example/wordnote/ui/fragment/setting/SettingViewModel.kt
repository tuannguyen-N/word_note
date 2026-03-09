package com.example.wordnote.ui.fragment.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.domain.usecase.SettingUseCase
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingUIEvent
import com.example.wordnote.utils.TimeLevel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingUseCase: SettingUseCase,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<NoteAlertSettingUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

//    val quiteHourList = noteAlertSettingUseCase.getQuiteHour().stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )

    fun onAction(action: SettingAction) {
        when (action) {
            is SettingAction.SetNotificationPost -> {
                performSetNotificationPost(action.canShow)
            }

            is SettingAction.SetMaxLearningWords -> {
                checkAndApplyMaxWords(action.maxWords)
            }

            is SettingAction.StartStudying -> {
                performStartStudying(action.id)
            }

            is SettingAction.StopStudying -> {
                performStopStudying(action.id)
            }

            is SettingAction.SetTimeRange -> {
                performSetTimeRange(action.startTime, action.endTime)
            }

            is SettingAction.ReplaceTime -> {
                performReplaceTimeLevel(action.level, action.amount)
            }

            is SettingAction.DeleteQuiteHour -> {
                performDeleteQuiteHour(action.id)
            }

            is SettingAction.SaveQuiteHour ->
                performSaveQuiteHour(action.startTime, action.endTime)

            is SettingAction.SetVoiceNotificationPost -> {
                performSetVoiceNotificationPost(action.canShow)
            }
        }
    }

    private fun performSetVoiceNotificationPost(canShow: Boolean) {
        AppPreferences.canSpeakingVoiceNotification = canShow
    }

    private fun performSaveQuiteHour(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            settingUseCase.insertQuiteHour(startTime, endTime)
        }
    }

    private fun performDeleteQuiteHour(id: Int) {
        viewModelScope.launch {
            settingUseCase.deleteQuiteHour(id)
        }
    }

    private fun performReplaceTimeLevel(level: TimeLevel, amount: Int) {
        val millis = amount * level.unitInMillis
        when (level) {
            TimeLevel.LEVEL_1 -> AppPreferences.timeLevel1 = millis
            TimeLevel.LEVEL_2 -> AppPreferences.timeLevel2 = millis
            TimeLevel.LEVEL_3 -> AppPreferences.timeLevel3 = millis
        }
    }

    private fun performSetTimeRange(startTime: Int, endTime: Int) {
        AppPreferences.startTimeNotification = startTime
        AppPreferences.endTimeNotification = endTime
        updateWordsNextTriggerAfterTimeRangeChange(startTime, endTime)
    }

    private fun updateWordsNextTriggerAfterTimeRangeChange(
        newStart: Int,
        newEnd: Int
    ) {
        viewModelScope.launch {
            settingUseCase.rescheduleWords(newStart, newEnd)
        }
    }

    private fun performStartStudying(id: Int) {
        viewModelScope.launch {
            settingUseCase.startStudying(id)
        }
    }

    private fun performStopStudying(id: Int) {
        viewModelScope.launch {
            settingUseCase.stopStudying(id)
        }
    }

    private fun checkAndApplyMaxWords(maxWords: Int) {
        Log.e("checkAndApplyMaxWords", "checkAndApplyMaxWords: $maxWords", )
        viewModelScope.launch {
            val currentMax = AppPreferences.maxWords
            val studyingCount = settingUseCase.countStudyingWord()

            if (studyingCount <= maxWords) {
                val isDecrease = maxWords < currentMax
                AppPreferences.maxWords = maxWords

                if (isDecrease && maxWords <= 10) {
                    sendUIEvent(NoteAlertSettingUIEvent.ShowDialogMeme)
                }
                if (!isDecrease && maxWords > 10) {
                    sendUIEvent(NoteAlertSettingUIEvent.ShowWowDialog)
                }
            } else {
                sendUIEvent(NoteAlertSettingUIEvent.ResetSeekBar(currentMax.toFloat() / 5))
                sendUIEvent(NoteAlertSettingUIEvent.ShowDialogWordAvailable(settingUseCase.getWordByStudiedTime()))
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
            val words = settingUseCase.getWordByStudiedTime()

            words.forEach { word ->
                settingUseCase.updateRemainingTime(word)
                settingUseCase.stopAlarm(word.id!!)
            }
        }
    }

    private fun restoreRemainingAlarms() {
        viewModelScope.launch {
            val words = settingUseCase.getAllWords()
            words.forEach { word ->
                settingUseCase.updateNextTrigger(word)
            }
        }
    }

    fun sendUIEvent(event: NoteAlertSettingUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}