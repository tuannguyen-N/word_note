package com.example.wordnote.data.api

import com.example.wordnote.domain.model.response.WordResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WordAPI {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordMeaning(
        @Path("word") word: String
    ): WordResponse
}
