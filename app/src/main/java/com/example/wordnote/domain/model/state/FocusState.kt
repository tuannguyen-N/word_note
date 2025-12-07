package com.example.wordnote.domain.model.state

data class FocusState(
    var totalSeconds: Long = 900,// 15 minutes
    var remainingSeconds: Long = 900,
    var isRunning: Boolean = false,
    var isStartFocussing: Boolean = false
)