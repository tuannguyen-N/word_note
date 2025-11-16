package com.example.wordnote.domain

import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.model.WordData

class LocalWordUseCase(private val wordRepository: WordRepository) {
    suspend fun upsertWord(wordData: WordData){
        wordRepository.upsertWord(wordData)
    }
}