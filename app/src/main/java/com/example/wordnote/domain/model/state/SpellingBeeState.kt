package com.example.wordnote.domain.model.state

import com.example.wordnote.domain.model.WordData

data class SpellingBeeState(
    val categoryId: Int? = null,
    val currentWord: WordData? = null,
    val incorrectionCount: Int = 0,
    val isSubmitEnabled: Boolean = true,
    val isShowAnswers: Boolean = false,
    val isBusy: Boolean = false,
    val isFinished: Boolean = false,
    val totalCount: Int = 0,
    val remainingCount: Int = 0,
    val isSingleMode: Boolean = false
)