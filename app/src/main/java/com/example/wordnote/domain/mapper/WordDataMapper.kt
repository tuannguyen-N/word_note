package com.example.wordnote.domain.mapper

import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.domain.model.WordData

fun WordData.toEntity(): WordEntity = WordEntity(word, definition)

fun WordEntity.toData(): WordData = WordData(word, definition)