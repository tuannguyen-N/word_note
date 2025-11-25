package com.example.wordnote.ui.activity.setting.note_alerts

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.databinding.ActivityNoteAlertSettingBinding
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.usecase.NoteAlertSettingUseCase
import com.example.wordnote.ui.activity.BaseActivity
import com.example.wordnote.ui.components.SeekBar
import com.example.wordnote.ui.dialog.CatDialog
import com.example.wordnote.ui.dialog.WordAvailableDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NoteAlertSettingActivity : BaseActivity<ActivityNoteAlertSettingBinding>(
    ActivityNoteAlertSettingBinding::inflate
) {
    private val seekBarValue = MutableStateFlow(AppPreferences.maxWords.toFloat() / 5)
    private val viewModel: NoteAlertViewModel by viewModels {
        NoteAlertSettingViewModelFactory(
            NoteAlertSettingUseCase(
                WordRepository(AppDatabase.getInstance(this).wordDao),
                AlarmScheduler(this)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onClickListener()
        setupView()
        collectUIEvent()
        setUpViewCompose()
    }

    private fun collectUIEvent() {
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is NoteAlertSettingUIEvent.ResetSeekBar -> resetSeekBar(event.oldValue)
                    is NoteAlertSettingUIEvent.ShowDialogMeme -> showMeme()
                    is NoteAlertSettingUIEvent.ShowDialogWordAvailable -> {
                        showDialogWordAvailable(event.list)
                    }
                }
            }
        }
    }

    private fun showDialogWordAvailable(list: List<WordData>) {
        val dialog = WordAvailableDialog(
            list,
            onStartStudying = {
                viewModel.onAction(NoteAlertSettingAction.StartStudying(it))
            },
            onStopStudying = {
                viewModel.onAction(NoteAlertSettingAction.StopStudying(it))
            }
        )
        dialog.show(supportFragmentManager, "WordAvailableDialog")
    }

    private fun showMeme() {
        val dialog = CatDialog()
        dialog.show(supportFragmentManager, "CatDialog")
    }

    private fun resetSeekBar(oldValue: Float) {
        seekBarValue.value = oldValue
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
                viewModel.onAction(NoteAlertSettingAction.SetNotificationPost(isChecked))
            }
        }
    }

    private fun setUpViewCompose() {
        binding.composeSeekBar.setContent {
            val value by seekBarValue.collectAsState()
            SeekBar(
                value,
                onValueChange = { newValue ->
                    seekBarValue.value = newValue
                },
                onValueChangeFinish = {
                    viewModel.onAction(NoteAlertSettingAction.SetMaxLearningWords(seekBarValue.value.toInt() * 5))
                }
            )
        }
    }
}