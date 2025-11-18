package com.example.wordnote.domain.model

data class WordData(
    val word: String,
    val phonetic: String,
    val meanings: List<MeaningData>,
    var level: Int = 0,
    var note: String = ""
)

data class MeaningData(
    val partOfSpeech: String,
    val definitions: List<DefinitionData>
)

data class DefinitionData(
    val definition: String,
    val example: String?,
    val synonyms: List<String>?,
)
