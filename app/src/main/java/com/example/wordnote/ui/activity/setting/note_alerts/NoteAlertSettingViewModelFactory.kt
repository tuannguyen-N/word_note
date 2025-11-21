package com.example.wordnote.ui.activity.setting.note_alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.usecase.NoteAlertSettingUseCase
import com.example.wordnote.ui.activity.main.MainViewModel

class NoteAlertSettingViewModelFactory(
    private val noteAlertSettingUseCase: NoteAlertSettingUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteAlertViewModel(noteAlertSettingUseCase) as T
    }
}