package com.example.wordnote.ui.fragment.focus

sealed interface FocusAction{
    object StartPauseFocus: FocusAction
    object OnStopFocus: FocusAction
    object OnPauseTime: FocusAction
    object OnResumeTime: FocusAction
    data class OnChangeTime(val value: Float): FocusAction
}
