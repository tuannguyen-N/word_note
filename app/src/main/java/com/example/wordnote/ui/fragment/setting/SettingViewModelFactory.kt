package com.example.wordnote.ui.fragment.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.usecase.SettingUseCase

class SettingViewModelFactory(
    private val settingUseCase: SettingUseCase,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(settingUseCase) as T
    }
}