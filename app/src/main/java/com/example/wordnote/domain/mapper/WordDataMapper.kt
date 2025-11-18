package com.example.wordnote.domain.mapper

import com.example.wordnote.adapter.AMeaningData
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.domain.model.DefinitionData
import com.example.wordnote.domain.model.MeaningData
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.model.response.Definition
import com.example.wordnote.domain.model.response.Meaning
import com.example.wordnote.domain.model.response.WordResponse
import com.example.wordnote.domain.model.response.WordResponseItem

fun WordResponseItem.toData(): WordData =
    WordData(
        word = word,
        phonetic = phonetic,
        meanings = meanings.map { it.toData() }
    )

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
        meaningsJson = Converters().fromMeanings(meanings)
    )

fun WordEntity.toData(): WordData =
    WordData(
        word = word.replaceFirstChar { it.uppercase() },
        level = level,
        phonetic = phonetic,
        meanings = Converters().toMeanings(meaningsJson),
        note = note
    )

fun WordData.toListMeaningData(): List<AMeaningData> {
    val listMeaning = mutableListOf<AMeaningData>()
    meanings.forEach { meaning ->
        val sortedDefinitions = meaning.definitions.sortedByDescending { it.example != null }
        val limitedDefinitions = sortedDefinitions.take(4)

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