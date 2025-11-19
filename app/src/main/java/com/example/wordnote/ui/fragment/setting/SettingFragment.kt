package com.example.wordnote.ui.fragment.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.SettingAdapter
import com.example.wordnote.databinding.FragmentSettingBinding
import com.example.wordnote.domain.model.ActivitySettingData
import com.example.wordnote.ui.activity.setting.note_alerts.NoteAlertSettingActivity
import com.example.wordnote.ui.fragment.BaseFragment

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private val settingAdapter = SettingAdapter(
        onClickAc = { action ->
            when (action) {
                SettingAction.OpenNoteAlertSetting -> openAc(NoteAlertSettingActivity())
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        binding.recyclerView.apply {
            adapter = settingAdapter
            layoutManager =
                GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        settingAdapter.setItemList(ActivitySettingData.getList())
    }

    private fun openAc(activity: Activity) {
        startActivity(
            Intent(
                requireContext(),
                activity::class.java
            )
        )
    }
}