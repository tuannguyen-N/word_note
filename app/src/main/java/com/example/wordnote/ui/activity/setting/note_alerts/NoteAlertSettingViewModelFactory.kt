package com.example.wordnote.ui.activity.setting.note_alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.usecase.SettingUseCase

class NoteAlertSettingViewModelFactory(
    private val settingUseCase: SettingUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteAlertViewModel(settingUseCase) as T
    }
}