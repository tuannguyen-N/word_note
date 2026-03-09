package com.example.wordnote.utils

import com.example.wordnote.data.AppPreferences

enum class WordLevel(val range: IntRange) {
    LEVEL_1(0..18),
    LEVEL_2(20..30),
    LEVEL_3(32..42),
    LEVEL_4(44..48);

    fun getDelay(): Long {
        return when (this) {
            LEVEL_1 -> AppPreferences.timeLevel1
            LEVEL_2 -> AppPreferences.timeLevel2
            LEVEL_3 -> AppPreferences.timeLevel3
            LEVEL_4 -> AppPreferences.timeLevel4
        }
    }

    companion object {
        fun fromScore(score: Int): WordLevel {
            return entries.firstOrNull { score in it.range } ?: LEVEL_1
        }

        fun fromLevelInt(level: Int): WordLevel {
            return when (level) {
                1 -> LEVEL_1
                2 -> LEVEL_2
                3 -> LEVEL_3
                4 -> LEVEL_4
                else -> LEVEL_1
            }
        }
    }
}

fun getDelay(wordLevel: Int): Long {
    return when (wordLevel) {
        1 -> AppPreferences.timeLevel1
        2 -> AppPreferences.timeLevel2
        3 -> AppPreferences.timeLevel3
        4 -> AppPreferences.timeLevel4
        else -> 0
    }
}

val WordLevel.nextTrigger: Long
    get() {
        val jitterMs = (-5..5).random() * 60 * 1000L
        return System.currentTimeMillis() + getDelay() + jitterMs
    }

enum class TimeLevel(val unitInMillis: Long) {
    LEVEL_1(60 * 1000L),
    LEVEL_2(24 * 60 * 60 * 1000L),
    LEVEL_3(7 * 24 * 60 * 60 * 1000L)
}

fun Long.toUnit(level: TimeLevel): Int {
    return (this / level.unitInMillis).toInt()
}

