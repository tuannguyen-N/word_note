package com.example.wordnote.data.mapper

import com.example.wordnote.adapter.AMeaningData
import com.example.wordnote.data.entities.CategoryEntity
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.DefinitionData
import com.example.wordnote.domain.model.MeaningData
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.response.Definition
import com.example.wordnote.domain.model.response.Meaning
import com.example.wordnote.domain.model.response.WordResponseItem

fun WordResponseItem.toData(): WordData =
    WordData(
        word = word,
        phonetic = resolvePhonetic(),
        meanings = meanings.map { it.toData() }
    )

fun WordResponseItem.resolvePhonetic(): String {
    return phonetic
        ?.takeIf { it.isNotBlank() }
        ?: phonetics
            ?.firstOrNull { !it.text.isNullOrBlank() }
            ?.text
        ?: "N/A"
}

fun Meaning.toData(): MeaningData =
    MeaningData(
        partOfSpeech = partOfSpeech,
        definitions = definitions.map { it.toData() }
    )

fun Definition.toData(): DefinitionData =
    DefinitionData(
        definition = definition,
        example = example,
        synonyms = synonyms.map { it.toString() }
    )

fun WordData.toEntity(): WordEntity =
    WordEntity(
        word = word.lowercase(),
        level = level,
        phonetic = phonetic,
        meaningsJson = Converters().fromMeanings(meanings),
        addedTime = addedTime,
        startStudiedTime = startStudiedTime,
        score = score,
        remainingTime = remainingTime,
        nextTriggerTime = nextTriggerTime
    )

fun WordEntity.toData(): WordData =
    WordData(
        id = id,
        word = word.replaceFirstChar { it.uppercase() },
        level = level,
        phonetic = phonetic,
        meanings = Converters().toMeanings(meaningsJson),
        note = note,
        addedTime = addedTime,
        startStudiedTime = startStudiedTime,
        score = score,
        remainingTime = remainingTime,
        nextTriggerTime = nextTriggerTime
    )

fun WordData.toListMeaningData(take: Int): List<AMeaningData> {
    val listMeaning = mutableListOf<AMeaningData>()
    meanings.forEach { meaning ->
        val sortedDefinitions = meaning.definitions.sortedByDescending { it.example != null }
        val limitedDefinitions = sortedDefinitions.take(take)

        limitedDefinitions.forEach { definitionData ->
            listMeaning.add(
                AMeaningData(
                    partOfSpeech = meaning.partOfSpeech,
                    definition = definitionData.definition,
                    example = definitionData.example,
                    synonyms = definitionData.synonyms
                )
            )
        }
    }
    return listMeaning
}

//------------ category ---------------/

fun CategoryEntity.toData(): CategoryData =
    CategoryData(
        id = id,
        name = name,
        description = description,
    )