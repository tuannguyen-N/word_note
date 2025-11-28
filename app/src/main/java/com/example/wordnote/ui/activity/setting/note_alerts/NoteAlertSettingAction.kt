package com.example.wordnote.ui.activity.setting.note_alerts

sealed interface NoteAlertSettingAction {
    data class SetNotificationPost(val canShow: Boolean) : NoteAlertSettingAction
    data class SetMaxLearningWords(val maxWords: Int) : NoteAlertSettingAction
    data class StartStudying(val id: Int) : NoteAlertSettingAction
    data class StopStudying(val id: Int) : NoteAlertSettingAction
    data class SetTimeRange(val startTime: Int, val endTime: Int) : NoteAlertSettingAction
}