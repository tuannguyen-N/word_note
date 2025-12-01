package com.example.wordnote.manager

import com.example.wordnote.domain.model.WordData

class SpellingBeeGameEngine(
    private val words: List<WordData>
) {
    private val remainingWords = words.toMutableList()
    private var currentWord: WordData? = null

    fun nextWord(): WordData? {
        if (remainingWords.isEmpty()) return null
        currentWord = remainingWords.random()
        remainingWords.remove(currentWord)
        return currentWord
    }

    fun verify(input: String): Boolean {
        val correct = currentWord?.word!!.equals(input, ignoreCase = true)
        return correct
    }
}