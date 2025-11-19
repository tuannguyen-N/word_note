package com.example.wordnote.data.repository

import com.example.wordnote.data.api.WordAPI
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.data.mapper.toData
import com.example.wordnote.data.mapper.toEntity
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WordRepository(
    private val dao: WordDao,
    private val api: WordAPI
) {
    suspend fun upsertWord(rawWord: String, level: Int): Result {
        return try {
            val word = rawWord.trim().lowercase()

            val existingWord = dao.getWord(word)
            if (existingWord != null) {
                return Result.AlreadyExists
            }

            val response = api.getWordMeaning(word)
            if (response.isEmpty()) {
                return Result.NotFound
            }

            val data = response.first().toData().copy(level = level, addedTime = System.currentTimeMillis())
            dao.upsertWord(data.toEntity())
            Result.Success(data)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteWord(word: WordData) {
        dao.deleteWord(word.word.lowercase())
    }

    suspend fun updateLevel(word: WordData) {
        dao.updateLevel(word.word.lowercase(), word.level)
    }

    suspend fun updateNote(word: WordData) {
        dao.updateNote(word.word.lowercase(), word.note)
    }

    fun getWordsOrderedByWord(): Flow<List<WordData>> =
        dao.getWordsOrderedByWord().map { list -> list.map { it.toData() } }

    fun getWordsByLevel(level: Int): Flow<List<WordData>> =
        dao.getWordsByLevel(level).map { list -> list.map { it.toData() } }
}