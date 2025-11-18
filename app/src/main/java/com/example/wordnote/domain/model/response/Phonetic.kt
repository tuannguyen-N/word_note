package com.example.wordnote.domain.model.response

data class Phonetic(
    val audio: String,
    val license: License,
    val sourceUrl: String,
    val text: String
)