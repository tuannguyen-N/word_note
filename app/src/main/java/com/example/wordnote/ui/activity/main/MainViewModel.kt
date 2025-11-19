package com.example.wordnote.ui.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<MainViewUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()


    fun onAction(action: MainAction) {
        when(action) {
            is MainAction.SendWordFromNotification -> performSendWordFromNotification(action.word)
        }
    }

    private fun performSendWordFromNotification(word: String) {
        sendUiEvent(MainViewUIEvent.SendWordFromNotification(word))
    }

    fun sendUiEvent(event: MainViewUIEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            _uiEvent.emit(event)
        }
    }
}