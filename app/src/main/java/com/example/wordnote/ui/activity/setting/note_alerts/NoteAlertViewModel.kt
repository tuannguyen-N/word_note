package com.example.wordnote.ui.activity.setting.note_alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.domain.usecase.NoteAlertSettingUseCase
import com.example.wordnote.utils.TimeLevel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class NoteAlertViewModel(
    private val noteAlertSettingUseCase: NoteAlertSettingUseCase,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<NoteAlertSettingUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val quiteHourList = noteAlertSettingUseCase.getQuiteHour().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

            is NoteAlertSettingAction.SetTimeRange -> {
                performSetTimeRange(action.startTime, action.endTime)
            }

            is NoteAlertSettingAction.ReplaceTime -> {
                performReplaceTimeLevel(action.level, action.amount)
            }

            is NoteAlertSettingAction.DeleteQuiteHour -> {
                performDeleteQuiteHour(action.id)
            }

            is NoteAlertSettingAction.SaveQuiteHour ->
                performSaveQuiteHour(action.startTime, action.endTime)
        }
    }

    private fun performSaveQuiteHour(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            noteAlertSettingUseCase.insertQuiteHour(startTime, endTime)
        }
    }

    private fun performDeleteQuiteHour(id: Int) {
        viewModelScope.launch {
            noteAlertSettingUseCase.deleteQuiteHour(id)
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

    private fun updateWordsNextTriggerAfterTimeRangeChange(newStart: Int, newEnd: Int) {
        viewModelScope.launch {
            val words = noteAlertSettingUseCase.getWordByStudiedTime()

            words.forEach { word ->

                val cal = Calendar.getInstance().apply {
                    timeInMillis = word.nextTriggerTime
                }

                val triggerMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

                if (triggerMinutes in newStart..newEnd) {
                    noteAlertSettingUseCase.scheduleWord(word)
                    return@forEach
                }

                val randomMinute = if (newStart <= newEnd) {
                    (newStart..newEnd).random()
                } else {
                    val minutesInDay = 24 * 60
                    ((newStart until minutesInDay) + (0..newEnd)).random()
                }

                cal.apply {
                    set(Calendar.HOUR_OF_DAY, randomMinute / 60)
                    set(Calendar.MINUTE, randomMinute % 60)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val newTriggerTime = cal.timeInMillis

                noteAlertSettingUseCase.stopAlarm(word.id!!)
                noteAlertSettingUseCase.updateNextTrigger(word.id, newTriggerTime)
                noteAlertSettingUseCase.scheduleWord(word.copy(nextTriggerTime = newTriggerTime))
            }
        }
    }


    private fun performStartStudying(id: Int) {
        viewModelScope.launch {
            noteAlertSettingUseCase.startStudying(id)
        }
    }

    private fun performStopStudying(id: Int) {
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
                if (!isDecrease && maxWords > 10) {
                    sendUIEvent(NoteAlertSettingUIEvent.ShowWowDialog)
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