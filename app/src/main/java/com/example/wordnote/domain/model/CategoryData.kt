package com.example.wordnote.domain.model

data class CategoryData(
    val id: Int? = null,
    val name: String,
    val description: String ="",
    val numberWordLevel1: Int = 0,
    val numberWordLevel2: Int = 0,
    val numberWordLevel3: Int = 0,
)