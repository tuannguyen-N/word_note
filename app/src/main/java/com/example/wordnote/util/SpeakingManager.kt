package com.example.wordnote.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class SpeakingManager(
    context: Context,
    private val onReady: (() -> Unit)? = null
) {
    private var tts: TextToSpeech? = null
    init {
        tts = TextToSpeech(context, { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts!!.language = Locale.US
                tts!!.setSpeechRate(0.8f)
                onReady?.invoke()
            }
        }, "com.google.android.tts")

        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(id: String?) {
                onDoneCallback?.invoke()
            }

            override fun onError(id: String?) {
                onDoneCallback?.invoke()
            }

            override fun onStart(id: String?) {}
        })
    }

    var onDoneCallback: (() -> Unit)? = null

    fun speak(word: String) {
        tts!!.speak(word, TextToSpeech.QUEUE_ADD, null, word)
    }

    fun destroy() {
        tts?.shutdown()
    }
}
