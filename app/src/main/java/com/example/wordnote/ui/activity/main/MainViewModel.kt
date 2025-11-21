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
            else -> {}
        }
    }

    fun sendUiEvent(event: MainViewUIEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            _uiEvent.emit(event)
        }
    }
}