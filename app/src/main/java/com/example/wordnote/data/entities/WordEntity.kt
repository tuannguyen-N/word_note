package com.example.wordnote.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordEntity(
    val word: String,
    val phonetic: String,
    val meaningsJson: String,
    val level: Int = 0,
    val note: String = "",

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)