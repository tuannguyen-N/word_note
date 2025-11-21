package com.example.wordnote.ui.activity.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.domain.usecase.ScheduleWordUseCase
import com.example.wordnote.util.SpeakingManager

class WordViewModelFactory(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager,
    private val scheduleWordUseCase: ScheduleWordUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WordViewModel(
            localWordUseCase, speakingManager, scheduleWordUseCase
        ) as T
    }
}