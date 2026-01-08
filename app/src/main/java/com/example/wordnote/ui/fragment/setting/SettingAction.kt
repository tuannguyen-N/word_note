package com.example.wordnote.ui.fragment.setting

import com.example.wordnote.utils.TimeLevel

sealed interface SettingAction{
    data class SetNotificationPost(val canShow: Boolean) : SettingAction
    data class SetMaxLearningWords(val maxWords: Int) : SettingAction
    data class StartStudying(val id: Int) : SettingAction
    data class StopStudying(val id: Int) : SettingAction
    data class SetTimeRange(val startTime: Int, val endTime: Int) : SettingAction
    data class ReplaceTime(val level: TimeLevel, val amount: Int): SettingAction
    data class DeleteQuiteHour(val id: Int): SettingAction
    data class SaveQuiteHour(val startTime: Long, val endTime: Long): SettingAction
    data class SetVoiceNotificationPost(val canShow: Boolean): SettingAction
}