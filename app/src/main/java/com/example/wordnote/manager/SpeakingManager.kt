package com.example.wordnote.manager

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.wordnote.data.AppPreferences
import java.util.Locale

enum class CountryVoice(val code: String, val locale: Locale) {
    US("us", Locale.US),
    UK("uk", Locale.UK),
    KOREAN("korean", Locale.KOREAN);

    companion object {
        fun getLocaleByCode(code: String): Locale? {
            return CountryVoice.entries.find { it.code == code }?.locale
        }
    }
}

class SpeakingManager(
    context: Context,
    private val onReady: (() -> Unit)? = null
) {
    var onDoneCallback: (() -> Unit)? = null
    private var tts: TextToSpeech? = null

    private var isReady = false
    private val pendingQueue = mutableListOf<String>()

    init {
        tts = TextToSpeech(context, { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts!!.setSpeechRate(0.8f)
                isReady = true
                onReady?.invoke()

                flushPending()
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

    fun speak(word: String) {
        if (!isReady) {
            pendingQueue.add(word)
            return
        }
        speakNow(word)
    }

    private fun speakNow(word: String) {
        val code = AppPreferences.codeVoice
        val locale = CountryVoice.getLocaleByCode(code!!) ?: Locale.US
        tts?.language = locale

        val targetVoice = when (code) {
            "us" -> tts?.voices?.find { it.name.startsWith("en-us") }
            "uk" -> tts?.voices?.find { it.name.startsWith("en-gb") }
            "korean" -> tts?.voices?.find { it.locale == Locale.KOREAN }
            else -> null
        }

        targetVoice?.let { tts?.voice = it }

        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, word)
    }

    private fun flushPending() {
        for (w in pendingQueue) {
            speakNow(w)
        }
        pendingQueue.clear()
    }

    fun destroy() {
        tts?.shutdown()
    }
}

