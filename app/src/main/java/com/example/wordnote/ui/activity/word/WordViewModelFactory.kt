package com.example.wordnote.ui.activity.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.manager.SpeakingManager

class WordViewModelFactory(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WordViewModel(
            localWordUseCase,
            speakingManager
        ) as T
    }
}