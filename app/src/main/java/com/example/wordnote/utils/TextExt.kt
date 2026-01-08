package com.example.wordnote.utils

fun String.normalizeWord() : String {
    return this.trim().lowercase()
}

fun String.upperFirstChar() : String {
    return this.replaceFirstChar { it.uppercase() }
}

fun String.lowerString() : String {
    return this.lowercase()
}