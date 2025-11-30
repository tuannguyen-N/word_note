package com.example.wordnote.ui.activity.setting.note_alerts

import com.example.wordnote.utils.TimeLevel

sealed interface NoteAlertSettingAction {
    data class SetNotificationPost(val canShow: Boolean) : NoteAlertSettingAction
    data class SetMaxLearningWords(val maxWords: Int) : NoteAlertSettingAction
    data class StartStudying(val id: Int) : NoteAlertSettingAction
    data class StopStudying(val id: Int) : NoteAlertSettingAction
    data class SetTimeRange(val startTime: Int, val endTime: Int) : NoteAlertSettingAction
    data class ReplaceTime(val level: TimeLevel, val amount: Int): NoteAlertSettingAction
}