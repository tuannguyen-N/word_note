package com.example.wordnote.util

sealed interface Result{
    object Success: Result
    object NotFound: Result
    object AlreadyExists: Result
    data class Error(val message: String): Result
}