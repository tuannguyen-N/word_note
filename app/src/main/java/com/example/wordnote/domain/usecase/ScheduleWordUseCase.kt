package com.example.wordnote.domain.usecase

import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.util.nextTrigger
import com.example.wordnote.util.toWordLevelOrNull

class ScheduleWordUseCase(private val alarmScheduler: AlarmScheduler) {
    fun scheduleWord(word: WordData) {
        val level = word.level.toWordLevelOrNull() ?: return
        alarmScheduler.scheduleWord(
            word,
            level.nextTrigger
        )
    }
}