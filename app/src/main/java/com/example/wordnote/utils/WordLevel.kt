package com.example.wordnote.utils

import com.example.wordnote.data.AppPreferences

enum class WordLevel(val range: IntRange, val delayMillis: Long) {
    LEVEL_1(0..9, AppPreferences.timeLevel1),
    LEVEL_2(10..15, AppPreferences.timeLevel2),
    LEVEL_3(16..21, AppPreferences.timeLevel3),
    LEVEL_4(22..Int.MAX_VALUE, AppPreferences.timeLevel4);

    companion object {
        fun fromScore(score: Int): WordLevel =
            entries.first { score in it.range }
    }
}

fun getDelay(wordLevel: Int): Long {
    return when (wordLevel) {
        1 -> AppPreferences.timeLevel1
        2 -> AppPreferences.timeLevel2
        3 -> AppPreferences.timeLevel3
        else -> 0
    }
}

val WordLevel.nextTrigger: Long
    get() = System.currentTimeMillis() + delayMillis
