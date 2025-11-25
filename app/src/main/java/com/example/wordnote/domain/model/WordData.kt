package com.example.wordnote.domain.model

data class WordData(
    val id: Int? = null,
    val word: String,
    val phonetic: String,
    val meanings: List<MeaningData>,
    var level: Int = 1,
    var note: String = "",
    var score: Int = 0,
    var addedTime: Long? = null,
    var startStudiedTime: Long = 0,
    var nextTriggerTime: Long = 0,
    var remainingTime: Long = 0
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
