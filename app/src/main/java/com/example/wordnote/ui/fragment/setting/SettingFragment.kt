package com.example.wordnote.ui.fragment.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.wordnote.R
import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.data.repository.QuiteHourRepository
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.databinding.FragmentSettingBinding
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.domain.usecase.SettingUseCase
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingUIEvent
import com.example.wordnote.ui.components.SeekBar
import com.example.wordnote.ui.dialog.CatDialog
import com.example.wordnote.ui.dialog.EditTimeBottomSheet
import com.example.wordnote.ui.dialog.WordAvailableDialog
import com.example.wordnote.ui.fragment.BaseFragment
import com.example.wordnote.utils.NotificationPermissionLauncher
import com.example.wordnote.utils.PermissionResult
import com.example.wordnote.utils.TimeLevel
import com.example.wordnote.utils.Utils
import com.example.wordnote.utils.onTextChanged
import com.example.wordnote.utils.setSafeOnClickListener
import com.example.wordnote.utils.toUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import nl.joery.timerangepicker.TimeRangePicker

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private lateinit var notificationPermissionLauncher: NotificationPermissionLauncher
//    private val settingAdapter = SettingAdapter(
//        onClickAc = { action ->
//            when (action) {
//                is SettingAction.OpenNoteAlertSetting -> {
//                    if (!notificationPermissionLauncher.isPermissionGranted()) {
//                        notificationPermissionLauncher.requestPermission()
//                    } else {
//                        openAc(NoteAlertSettingActivity())
//                    }
//                }
//
//                is SettingAction.OpenVoiceSetting -> openAc(VoiceSettingActivity())
//            }
//        }
//    )

    private val settingViewModel: SettingViewModel by viewModels {
        val appDatabase = AppDatabase.getInstance(requireContext())
        SettingViewModelFactory(
            SettingUseCase(
                WordRepository(appDatabase.wordDao),
                QuiteHourRepository(appDatabase.quiteHourDao),
                AlarmScheduler(requireContext())
            )
        )
    }

    private val seekBarValue = MutableStateFlow(AppPreferences.maxWords.toFloat() / 5)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPermission()
        initView()
        initCompose()
        setupClickListeners()
        collectUiEvents()
    }

    private fun collectUiEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingViewModel.uiEvent.collect(::handleUiEvent)
            }
        }
    }

    private fun handleUiEvent(event: NoteAlertSettingUIEvent) {
        when (event) {
            is NoteAlertSettingUIEvent.ResetSeekBar ->
                seekBarValue.value = event.oldValue

            is NoteAlertSettingUIEvent.ShowDialogMeme ->
                showCatDialog(R.drawable.img_cat_looking)

            is NoteAlertSettingUIEvent.ShowWowDialog ->
                showCatDialog(R.drawable.image_wow)

            is NoteAlertSettingUIEvent.ShowDialogWordAvailable ->
                showWordAvailableDialog(event.list)
        }
    }

    private fun showWordAvailableDialog(list: List<WordData>) {
        WordAvailableDialog(
            list,
            onStartStudying = {
                settingViewModel.onAction(
                    SettingAction.StartStudying(it)
                )
            },
            onStopStudying = {
                settingViewModel.onAction(
                    SettingAction.StopStudying(it)
                )
            }
        ).show(parentFragmentManager, "WordAvailableDialog")
    }

    private fun showCatDialog(imageRes: Int) {
        CatDialog(imageRes)
            .show(parentFragmentManager, "CatDialog")
    }

    private fun openEditTimeBottomSheet() {
        EditTimeBottomSheet(
            onEditTime = ::updateTimeView,
            onDoneEditTime = { start, end ->
                settingViewModel.onAction(
                    SettingAction.SetTimeRange(start, end)
                )
            }
        ).show(parentFragmentManager, "EditTimeBottomSheet")
    }


    private fun initView() = binding.apply {
        switchAppNotification.isChecked = AppPreferences.canPostNotifications
        switchVoiceNotifying.isChecked = AppPreferences.canSpeakingVoiceNotification

        btnEditTime.text = buildTimeRangeText(
            AppPreferences.startTimeNotification,
            AppPreferences.endTimeNotification
        )

        etLv1.setText(
            AppPreferences.timeLevel1.toUnit(TimeLevel.LEVEL_1).toString()
        )

        setupAutoScrollOnFocus(etLv1)
    }

    private fun buildTimeRangeText(start: Int, end: Int): String {
        return "${Utils.formatTimeMinutes(start)} - ${Utils.formatTimeMinutes(end)}"
    }

    private fun setupAutoScrollOnFocus(view: View) {
        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.nestedScroll.postDelayed({
                    binding.nestedScroll.smoothScrollTo(0, view.top + 200)
                }, 150)
            }
        }
    }

    private fun initCompose() {
        binding.composeSeekBar.setContent {
            val value by seekBarValue.collectAsState()
            SeekBar(
                value = value,
                onValueChange = { seekBarValue.value = it },
                onValueChangeFinish = {
                    val latestValue = seekBarValue.value
                    settingViewModel.onAction(
                        SettingAction.SetMaxLearningWords(latestValue.toInt() * 5)
                    )
                }
            )
        }
    }

    private fun setupClickListeners() = binding.apply {

        switchAppNotification.setOnCheckedChangeListener { _, checked ->
            settingViewModel.onAction(
                SettingAction.SetNotificationPost(checked)
            )
        }

        switchVoiceNotifying.setOnCheckedChangeListener { _, checked ->
            settingViewModel.onAction(
                SettingAction.SetVoiceNotificationPost(checked)
            )
        }

        btnEditTime.setSafeOnClickListener {
            openEditTimeBottomSheet()
        }

        setupLevelInput(etLv1)
    }

    private fun setupLevelInput(
        editText: EditText,
    ) {
        editText.onTextChanged { text ->

            val value = text.toIntOrNull()
                ?: return@onTextChanged run {
                    editText.error = "Must be a number"
                }

            if (value < 20) {
                editText.error = "Minimum is 20 minutes"
                return@onTextChanged
            }

            val finalValue = value.coerceAtMost(60)

            if (finalValue != value) {
                editText.setText(finalValue.toString())
                editText.setSelection(editText.text.length)
            }

            settingViewModel.onAction(
                SettingAction.ReplaceTime(TimeLevel.LEVEL_1, finalValue)
            )
        }
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

    private fun initPermission() {
        notificationPermissionLauncher = NotificationPermissionLauncher(
            caller = this,
            activityProvider = { requireActivity() },
            onResult = { result ->
                when (result) {
                    PermissionResult.Denied -> showToast("Was not Granted")
                    PermissionResult.Granted -> {
                        showToast("Granted")
                        AppPreferences.canPostNotifications = true
                    }

                    PermissionResult.NeedOpenSettings -> showToast("Need Open Settings to show notification")
                    PermissionResult.ShowRationaleDialog -> showToast("Show Rationale Dialog")
                }
            }
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}