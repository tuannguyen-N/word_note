package com.example.wordnote.data.repository

import android.content.Context
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.domain.mapper.toEntity
import com.example.wordnote.domain.model.WordData

class WordRepository(
    private val context: Context,
    private val dao: WordDao
) {
    suspend fun upsertWord(word: WordData){
        dao.upsertWord(word.toEntity())
    }
}