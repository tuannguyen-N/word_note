package com.example.wordnote.ui.fragment.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.SettingAdapter
import com.example.wordnote.data.AppPreferences
import com.example.wordnote.databinding.FragmentSettingBinding
import com.example.wordnote.domain.model.item.SettingItem
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingActivity
import com.example.wordnote.ui.activity.setting.voice.VoiceSettingActivity
import com.example.wordnote.ui.fragment.BaseFragment
import com.example.wordnote.utils.NotificationPermissionLauncher
import com.example.wordnote.utils.PermissionResult

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private lateinit var notificationPermissionLauncher: NotificationPermissionLauncher
    private val settingAdapter = SettingAdapter(
        onClickAc = { action ->
            when (action) {
                is SettingAction.OpenNoteAlertSetting -> {
                    if (!notificationPermissionLauncher.isPermissionGranted()) {
                        notificationPermissionLauncher.requestPermission()
                    } else {
                        openAc(NoteAlertSettingActivity())
                    }
                }

                is SettingAction.OpenVoiceSetting -> openAc(VoiceSettingActivity())
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNotificationPermissionLauncher()
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.recyclerView.apply {
            adapter = settingAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        settingAdapter.setItemList(SettingItem.getList())
    }

    private fun openAc(activity: Activity) {
        startActivity(
            Intent(
                requireContext(),
                activity::class.java
            )
        )
    }

    private fun initNotificationPermissionLauncher() {
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