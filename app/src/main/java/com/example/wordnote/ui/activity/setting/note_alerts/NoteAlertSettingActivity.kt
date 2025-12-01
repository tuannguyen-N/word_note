package com.example.wordnote.ui.activity.setting.note_alerts

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.wordnote.R
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
import com.example.wordnote.ui.dialog.EditTimeBottomSheet
import com.example.wordnote.ui.dialog.WordAvailableDialog
import com.example.wordnote.utils.NotificationPermissionLauncher
import com.example.wordnote.utils.PermissionResult
import com.example.wordnote.utils.TimeLevel
import com.example.wordnote.utils.Utils
import com.example.wordnote.utils.onTextChanged
import com.example.wordnote.utils.toUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nl.joery.timerangepicker.TimeRangePicker

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
        setupView()
        onClickListener()
        collectUIEvent()
        setUpViewCompose()
    }

    private fun collectUIEvent() {
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is NoteAlertSettingUIEvent.ResetSeekBar -> resetSeekBar(event.oldValue)
                    is NoteAlertSettingUIEvent.ShowDialogMeme -> showMeme()
                    is NoteAlertSettingUIEvent.ShowWowDialog -> showWowDialog()
                    is NoteAlertSettingUIEvent.ShowDialogWordAvailable -> {
                        showDialogWordAvailable(event.list)
                    }
                }
            }
        }
    }

    private fun showWowDialog() {
        val dialog = CatDialog(R.drawable.image_wow)
        dialog.show(supportFragmentManager, "CatDialog")
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
        val dialog = CatDialog(R.drawable.img_cat_looking)
        dialog.show(supportFragmentManager, "CatDialog")
    }

    private fun resetSeekBar(oldValue: Float) {
        seekBarValue.value = oldValue
    }

    @SuppressLint("SetTextI18n")
    private fun setupView() {
        binding.apply {
            switchAppNotification.isChecked = AppPreferences.canPostNotifications
            val startTime = Utils.formatTimeMinutes(AppPreferences.startTimeNotification)
            val endTime = Utils.formatTimeMinutes(AppPreferences.endTimeNotification)
            btnEditTime.text = "$startTime - $endTime"

            etLv1.setText(AppPreferences.timeLevel1.toUnit(TimeLevel.LEVEL_1).toString())
            etLv2.setText(AppPreferences.timeLevel2.toUnit(TimeLevel.LEVEL_2).toString())
            etLv3.setText(AppPreferences.timeLevel3.toUnit(TimeLevel.LEVEL_3).toString())
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
            btnEditTime.setOnClickListener {
                openEditTimeBottomSheet()
            }

            setupLevelInput(binding.etLv1, TimeLevel.LEVEL_1, 1440) // max a days
            setupLevelInput(binding.etLv2, TimeLevel.LEVEL_2, 7)    // max 1 week
            setupLevelInput(binding.etLv3, TimeLevel.LEVEL_3, 4)    // max 1 month
        }
    }

    private fun setupLevelInput(
        editText: EditText,
        level: TimeLevel,
        maxValue: Int
    ) {
        editText.onTextChanged { text ->
            if (text.isBlank()) {
                editText.error = "Không được để trống"
                return@onTextChanged
            }

            val value = text.toIntOrNull()
            if (value == null || value < 0) {
                editText.error = "Giá trị không hợp lệ"
                return@onTextChanged
            }

            val finalValue = if (value > maxValue) {
                editText.post {
                    editText.setText(maxValue.toString())
                    editText.setSelection(editText.text.length)
                }
                maxValue
            } else {
                value
            }

            viewModel.onAction(NoteAlertSettingAction.ReplaceTime(level, finalValue))
        }
    }

    private fun openEditTimeBottomSheet() {
        val bs = EditTimeBottomSheet(
            onEditTime = { startTime, endTime ->
                updateTimeView(startTime, endTime)
            },
            onDoneEditTime = { startTime, endTime ->
                viewModel.onAction(NoteAlertSettingAction.SetTimeRange(startTime, endTime))
            }
        )
        bs.show(supportFragmentManager, "EditTimeBottomSheet")
    }

    @SuppressLint("SetTextI18n")
    private fun updateTimeView(startTime: TimeRangePicker.Time, endTime: TimeRangePicker.Time) {
        binding.btnEditTime.text = "${
            Utils.formatTime(
                startTime.hour,
                startTime.minute
            )
        } - ${Utils.formatTime(endTime.hour, endTime.minute)}"
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