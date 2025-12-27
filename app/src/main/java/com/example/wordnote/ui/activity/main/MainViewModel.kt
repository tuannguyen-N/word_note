package com.example.wordnote.ui.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.state.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _uiEvent = MutableSharedFlow<MainViewUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.OnChangeDeleteMode -> performChangeDeleteMode(action.isDeleteMode)
            is MainAction.RequestDelete -> performRequestDelete()
        }
    }

    private fun performRequestDelete(){
        sendUiEvent(MainViewUIEvent.RequestDelete)
    }

    private fun performChangeDeleteMode(isDeleteMode: Boolean) {
        sendUiEvent(MainViewUIEvent.ChangeDeleteMode(isDeleteMode))
    }

    fun sendUiEvent(event: MainViewUIEvent) {
        viewModelScope.launch(Dispatchers.Main) {
            _uiEvent.emit(event)
        }
    }
}