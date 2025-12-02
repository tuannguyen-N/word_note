package com.example.wordnote.domain.model

data class SpellingBeeState(
    val categoryId: Int? = null,
    val words: List<WordData> = emptyList(),
    val currentWord: WordData? = null,
    val incorrectionCount: Int = 0,
    val isSubmitEnabled: Boolean = true,
    val isShowAnswers: Boolean = false,
    val isBusy: Boolean = false,
    val isFinished: Boolean = false,
)
