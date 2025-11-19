package com.example.wordnote.ui.activity.setting.note_alerts

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordnote.R
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.databinding.ActivityNoteAlertSettingBinding
import com.example.wordnote.ui.activity.BaseActivity

class NoteAlertSettingActivity : BaseActivity<ActivityNoteAlertSettingBinding>(
    ActivityNoteAlertSettingBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClickListener()
        setupView()
    }

    private fun setupView() {
        binding.apply {
            switchAppNotification.isChecked = AppPreferences.canPostNotifications
        }
    }

    private fun onClickListener() {
        binding.apply {
            btnBack.setOnClickListener {
                finish()
            }
            switchAppNotification.setOnCheckedChangeListener { _, isChecked ->
                AppPreferences.canPostNotifications = isChecked
            }
        }
    }
}