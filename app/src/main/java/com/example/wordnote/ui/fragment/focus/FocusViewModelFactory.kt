package com.example.wordnote.ui.fragment.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FocusViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FocusViewModel() as T
    }
}