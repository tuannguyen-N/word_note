package com.example.wordnote.domain.usecase

import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.entities.QuiteHourEntity
import com.example.wordnote.data.repository.QuiteHourRepository
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.model.WordData
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class SettingUseCase(
    private val wordRepository: WordRepository,
    private val quiteHourRepository: QuiteHourRepository,
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

    suspend fun updateNextTrigger(wordId: Int, newTrigger: Long) {
        wordRepository.updateNextTrigger(wordId, newTrigger)
    }

    fun scheduleWord(word: WordData) {
        alarmScheduler.scheduleWord(word, word.nextTriggerTime)
    }

    suspend fun countStudyingWord(): Int = wordRepository.countStudyingWords()

    fun stopAlarm(wordId: Int) {
        alarmScheduler.stopScheduleWord(wordId)
    }

    suspend fun rescheduleWords(
        start: Int,
        end: Int
    ) {
        val words = getWordByStudiedTime()

        words.forEach { word ->
            val triggerMinute = getMinuteOfDay(word.nextTriggerTime)

            if (isInTimeRange(triggerMinute, start, end)) {
                scheduleWord(word)
                return@forEach
            }

            val newMinute = randomMinuteInRange(start, end)
            val newTime = buildTime(word.nextTriggerTime, newMinute)

            stopAlarm(word.id!!)
            updateNextTrigger(word.id, newTime)
            scheduleWord(word.copy(nextTriggerTime = newTime))
        }
    }

    /** Helper functions **/

    private fun getMinuteOfDay(timeMillis: Long): Int {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        return cal.get(Calendar.HOUR_OF_DAY) * 60 +
                cal.get(Calendar.MINUTE)
    }

    private fun isInTimeRange(
        time: Int,
        start: Int,
        end: Int
    ): Boolean {
        return if (start <= end) {
            time in start..end
        } else {
            time !in (end + 1)..<start
        }
    }

    private fun randomMinuteInRange(
        start: Int,
        end: Int
    ): Int {
        return if (start <= end) {
            (start..end).random()
        } else {
            if (kotlin.random.Random.nextBoolean()) {
                (start until 1440).random()
            } else {
                (0..end).random()
            }
        }
    }

    private fun buildTime(
        oldTimeMillis: Long,
        newMinute: Int
    ): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = oldTimeMillis
            set(Calendar.HOUR_OF_DAY, newMinute / 60)
            set(Calendar.MINUTE, newMinute % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    /** Quite hour **/

    fun getQuiteHour(): Flow<List<QuiteHourEntity>> {
        return quiteHourRepository.getAllQuiteHour()
    }

    suspend fun insertQuiteHour(startTime: Long, endTime: Long) {
        quiteHourRepository.insertQuiteHour(startTime, endTime)
    }

    suspend fun deleteQuiteHour(id: Int) {
        quiteHourRepository.deleteQuiteHour(id)
    }
}