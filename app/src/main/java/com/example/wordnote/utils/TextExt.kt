package com.example.wordnote.utils

fun String.normalizeWord() : String {
    return this.trim().lowercase()
}