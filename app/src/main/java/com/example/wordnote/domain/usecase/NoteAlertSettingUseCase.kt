package com.example.wordnote.domain.usecase

import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.model.WordData

class NoteAlertSettingUseCase(
    private val wordRepository: WordRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend fun getAllWords(): List<WordData> = wordRepository.getAllWordsSync()
    suspend fun getWordByStudiedTime(): List<WordData> = wordRepository.getWordByStudiedTime()

    suspend fun updateRemainingTime(word: WordData) {
        if (word.nextTriggerTime > 0 && word.startStudiedTime > 0) {
            val now = System.currentTimeMillis()
            val remaining = word.nextTriggerTime - now
            if (remaining > 0) wordRepository.updateRemainingTime(word.id!!, remaining)
        }
    }

    suspend fun startStudying(id: Int) {
        wordRepository.updateStudiedTime(id, System.currentTimeMillis())
    }

    suspend fun stopStudying(id: Int) {
        wordRepository.updateStudiedTime(id, 0)
    }

    suspend fun updateNextTrigger(word: WordData) {
        val now = System.currentTimeMillis()
        if (word.remainingTime > 0 && word.startStudiedTime > 0) {
            val newTrigger = now + word.remainingTime
            wordRepository.updateNextTrigger(word.id!!, newTrigger)
            alarmScheduler.scheduleWord(word, newTrigger)
        }
    }

    suspend fun countStudyingWord(): Int = wordRepository.countStudyingWords()

    fun stopAlarm(wordId: Int) {
        alarmScheduler.stopScheduleWord(wordId)
    }
}