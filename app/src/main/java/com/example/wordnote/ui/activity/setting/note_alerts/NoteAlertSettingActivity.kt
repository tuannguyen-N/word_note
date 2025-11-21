package com.example.wordnote.ui.activity.setting.note_alerts

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordnote.R
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.api.RetrofitInstance
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.databinding.ActivityNoteAlertSettingBinding
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.domain.usecase.NoteAlertSettingUseCase
import com.example.wordnote.ui.activity.BaseActivity

class NoteAlertSettingActivity : BaseActivity<ActivityNoteAlertSettingBinding>(
    ActivityNoteAlertSettingBinding::inflate
) {
    private val viewModel: NoteAlertViewModel by viewModels {
        NoteAlertSettingViewModelFactory(
            NoteAlertSettingUseCase(
                LocalWordUseCase(
                    WordRepository(
                        AppDatabase.getInstance(this).wordDao,
                    )
                )
            )
        )
    }

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