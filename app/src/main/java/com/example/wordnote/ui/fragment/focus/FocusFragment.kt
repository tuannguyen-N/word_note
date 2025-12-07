package com.example.wordnote.ui.fragment.focus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.wordnote.databinding.FragmentFocusBinding
import com.example.wordnote.domain.model.state.FocusState
import com.example.wordnote.ui.components.SeekBarTime
import com.example.wordnote.ui.dialog.ConfirmStopFocusDialog
import com.example.wordnote.ui.fragment.BaseFragment
import kotlinx.coroutines.launch

class FocusFragment : BaseFragment<FragmentFocusBinding>(FragmentFocusBinding::inflate) {
    private val focusViewModel: FocusViewModel by viewModels {
        FocusViewModelFactory()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewCompose()
        collectState()
        setOnClickListener()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                focusViewModel.state.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateUI(state: FocusState) {
        binding.apply {
            tvHour.text = String.format("%02d", state.remainingSeconds / 60)
            tvMinute.text = String.format("%02d", state.remainingSeconds % 60)
            btnPlay.isSelected = state.isRunning
            btnStopFocus.visibility = if (state.isStartFocussing) View.VISIBLE else View.GONE
            composeViewSeekbar.visibility = if (state.isStartFocussing) View.GONE else View.VISIBLE
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            btnPlay.setOnClickListener {
                focusViewModel.onAction(FocusAction.StartPauseFocus)
            }

            btnStopFocus.setOnClickListener {
                focusViewModel.onAction(FocusAction.OnPauseTime)
                showDialogConfirm()
            }
        }
    }

    private fun showDialogConfirm() {
        val dialog = ConfirmStopFocusDialog(
            onDismiss = {
                focusViewModel.onAction(FocusAction.OnResumeTime)
            },
            onStopFocus = {
                focusViewModel.onAction(FocusAction.OnStopFocus)
            }
        )
        dialog.show(childFragmentManager, "ConfirmStopFocusDialog")
    }

    private fun setUpViewCompose() {
        binding.composeViewSeekbar.setContent {
            val value by focusViewModel.timeValue.collectAsState()
            SeekBarTime(
                value,
                onValueChange = { newValue ->
                    focusViewModel.onAction(FocusAction.OnChangeTime(newValue))
                },
                onValueChangeFinish = {

                }
            )
        }
    }
}