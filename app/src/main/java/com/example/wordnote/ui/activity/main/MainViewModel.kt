package com.example.wordnote.ui.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val uiEvent = MutableSharedFlow<MainViewUIEvent>()

    fun onAction(action: MainAction) {
        when(action) {
            else -> {}
        }
    }

    fun sendUiEvent(event: MainViewUIEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            uiEvent.emit(event)
        }
    }
}