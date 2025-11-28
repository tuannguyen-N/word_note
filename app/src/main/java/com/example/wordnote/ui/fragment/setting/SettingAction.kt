package com.example.wordnote.ui.fragment.setting

sealed interface SettingAction{
    object OpenNoteAlertSetting: SettingAction
    object OpenVoiceSetting: SettingAction
}