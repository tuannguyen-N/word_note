package com.example.wordnote.domain.usecase

import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.util.Result

class LocalWordUseCase(private val wordRepository: WordRepository) {
    suspend fun upsertWord(word: String, level: Int, categoryId: Int): Result =
        wordRepository.upsertWord(word, level, categoryId)

    suspend fun deleteWord(word: WordData) {
        wordRepository.deleteWord(word)
    }

    suspend fun updateLevel(word: WordData) {
        wordRepository.updateLevel(word)
    }

    suspend fun updateNote(word: WordData) {
        wordRepository.updateNote(word)
    }

    fun getWordsOrderedByWord() = wordRepository.getWordsOrderedByWord()

    fun getWordsByLevel(level: Int) = wordRepository.getWordsByLevel(level)

    fun getWordsByCategory(id: Int) = wordRepository.getWordsByCategory(id)

    fun getWordsByCategoryAndLevel(id: Int, level: Int) =
        wordRepository.getWordsByCategoryAndLevel(id, level)
}