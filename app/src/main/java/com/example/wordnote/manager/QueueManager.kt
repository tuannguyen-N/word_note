package com.example.wordnote.manager

import java.util.LinkedList

object QueueManager {
    private val queue = LinkedList<String>()

    fun add(word: String) = queue.add(word)

    fun next(): String? = if (queue.isNotEmpty()) queue.poll() else null
}

