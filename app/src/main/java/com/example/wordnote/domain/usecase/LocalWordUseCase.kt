package com.example.wordnote.domain.usecase

import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.utils.Result
import com.example.wordnote.utils.WordLevel
import com.example.wordnote.utils.nextTrigger

class LocalWordUseCase(
    private val wordRepository: WordRepository,
    private val alarmScheduler: AlarmScheduler? = null
) {
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

    suspend fun startStudying(word: WordData) {
        val currentTime = System.currentTimeMillis()
        val timeLevel = WordLevel.fromScore(word.score)
        wordRepository.updateStudiedTime(word.id!!, currentTime)
        wordRepository.updateNextTrigger(word.id, timeLevel.nextTrigger)
        alarmScheduler?.scheduleWord(word, timeLevel.nextTrigger)
    }

    suspend fun stopStudying(wordId: Int) {
        wordRepository.updateStudiedTime(wordId, 0)
        alarmScheduler?.stopScheduleWord(wordId)
    }

    fun getWordsOrderedByWord() = wordRepository.getWordsOrderedByWord()

    fun getWordsByLevel(level: Int) = wordRepository.getWordsByLevel(level)

    fun getWordsByCategory(id: Int) = wordRepository.getWordsByCategory(id)

    fun getWordsByCategoryAndLevel(id: Int, level: Int) =
        wordRepository.getWordsByCategoryAndLevel(id, level)

    suspend fun countStudyingWords() : Int = wordRepository.countStudyingWords()
}