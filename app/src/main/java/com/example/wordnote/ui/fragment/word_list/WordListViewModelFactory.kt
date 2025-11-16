package com.example.wordnote.ui.fragment.word_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.LocalWordUseCase
import com.example.wordnote.util.SpeakingManager

class WordListViewModelFactory(
    private val localWordUseCase: LocalWordUseCase,
    private val speakingManager: SpeakingManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WordListViewModel(
            localWordUseCase, speakingManager
        ) as T
    }
}