package com.example.wordnote.util

import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.WordData

sealed interface Result {
    data class Success(val word: WordData? = null) : Result
    object NotFound : Result
    object AlreadyExists : Result
    data class Error(val message: String) : Result
    data class AlreadyExistsInCategories(
        val word: WordData,
        val categoryNames: List<String>
    ) : Result
}