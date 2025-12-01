package com.example.wordnote.domain.model

data class SpellingBeeState(
    val words: List<WordData> = emptyList(),
    val currentWord: WordData? = null,
    val isSubmitEnabled : Boolean = true
)