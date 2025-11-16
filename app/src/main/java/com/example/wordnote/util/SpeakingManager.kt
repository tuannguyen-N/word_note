package com.example.wordnote.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeakingManager(context: Context) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts!!.language = Locale.US
            }
        }
    }

    fun speakingWord(word: String){
        tts!!.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}