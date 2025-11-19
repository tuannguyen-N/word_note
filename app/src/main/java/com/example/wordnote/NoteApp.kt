package com.example.wordnote

import android.app.Application
import com.example.wordnote.data.AppPreferences

class NoteApp: Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}