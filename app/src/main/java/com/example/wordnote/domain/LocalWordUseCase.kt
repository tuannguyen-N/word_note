package com.example.wordnote.domain

import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.util.Result

class LocalWordUseCase(private val wordRepository: WordRepository) {
    suspend fun upsertWord(word: String, level: Int): Result =
        wordRepository.upsertWord(word, level)

    suspend fun deleteWord(word: WordData){
        wordRepository.deleteWord(word)
    }

    suspend fun updateLevel(word: WordData){
        wordRepository.updateLevel(word)
    }

    suspend fun updateNote(word: WordData){
        wordRepository.updateNote(word)
    }

    fun getWordsOrderedByWord() = wordRepository.getWordsOrderedByWord()

    fun getWordsByLevel(level: Int) = wordRepository.getWordsByLevel(level)
}