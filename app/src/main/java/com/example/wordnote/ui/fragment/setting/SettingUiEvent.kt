package com.example.wordnote.ui.fragment.setting

import com.example.wordnote.domain.model.WordData
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingUIEvent

sealed interface SettingUiEvent {
    data class ResetSeekBar(val oldValue: Float): SettingUiEvent
    object ShowDialogMeme: SettingUiEvent
    object ShowWowDialog: SettingUiEvent
    data class ShowDialogWordAvailable(val list: List<WordData>): SettingUiEvent
}