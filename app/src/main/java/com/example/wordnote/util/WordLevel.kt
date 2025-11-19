package com.example.wordnote.util

enum class WordLevel(val delayMillis: Long) {
    LEVEL_1(30 * 1000L),           // 30 minutes
    LEVEL_2(3 * 24 * 60 * 60 * 1000L),  // 3 days
    LEVEL_3(7 * 24 * 60 * 60 * 1000L);  // 1 week
}
fun Int.toWordLevelOrNull(): WordLevel? = when(this) {
    1 -> WordLevel.LEVEL_1
    2 -> WordLevel.LEVEL_2
    3 -> WordLevel.LEVEL_3
    else -> null
}

val WordLevel.nextTrigger: Long
    get() = System.currentTimeMillis() + delayMillis
