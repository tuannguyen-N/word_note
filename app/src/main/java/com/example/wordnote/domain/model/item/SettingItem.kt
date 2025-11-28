package com.example.wordnote.domain.model.item

import com.example.wordnote.R
import com.example.wordnote.ui.fragment.setting.SettingAction

data class SettingItem(
    val icon: Int,
    val title: Int,
    val detail: Int,
    val action: SettingAction
) {
    companion object {
        fun getList(): List<SettingItem> =
            listOf(
                SettingItem(
                    R.drawable.ic_notification_setting,
                    title = R.string.note_alerts,
                    detail = R.string.configure_how_your_nalerts_are_received,
                    action = SettingAction.OpenNoteAlertSetting
                ),
                SettingItem(
                    R.drawable.ic_volume,
                    title = R.string.voice_setting,
                    detail = R.string.configure_how_your_voice_is_received,
                    action = SettingAction.OpenVoiceSetting
                )
            )
    }
}
