package com.example.wordnote.manager

import com.example.wordnote.utils.WordLevel

class WordLevelManager {
    fun calculateLevelFromScore(score: Int): WordLevel {
        return WordLevel.fromScore(score)
    }

    fun nextTriggerTime(level: WordLevel): Long {
        return System.currentTimeMillis() + level.delayMillis
    }
}