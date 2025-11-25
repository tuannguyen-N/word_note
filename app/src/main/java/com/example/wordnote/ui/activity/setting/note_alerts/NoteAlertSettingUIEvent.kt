package com.example.wordnote.ui.activity.setting.note_alerts

import com.example.wordnote.domain.model.WordData

sealed interface NoteAlertSettingUIEvent{
    data class ResetSeekBar(val oldValue: Float): NoteAlertSettingUIEvent
    object ShowDialogMeme: NoteAlertSettingUIEvent
    data class ShowDialogWordAvailable(val list: List<WordData>): NoteAlertSettingUIEvent
}
