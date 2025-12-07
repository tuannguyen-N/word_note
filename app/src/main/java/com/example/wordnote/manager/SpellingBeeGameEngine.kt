package com.example.wordnote.manager

import com.example.wordnote.domain.model.WordData

class SpellingBeeGameEngine(words: List<WordData>) {

    private val remainingWords = words.toMutableList()
    private var current: WordData? = null

    fun next(): WordData? {
        if (remainingWords.isEmpty()) return null
        current = remainingWords.random()
        return current
    }

    fun verify(input: String): Boolean {
        val correct = current?.word.equals(input, ignoreCase = true)
        if (correct) remainingWords.remove(current)
        return correct
    }

    fun isFinished(): Boolean = remainingWords.isEmpty()
}

