package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.BottomSheetFullWordsBinding

class FullWordsBottomSheet(
    private val onGoToSetting: () -> Unit,
) : BaseDialog<BottomSheetFullWordsBinding>(
    BottomSheetFullWordsBinding::inflate
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
        setUpView()
    }

    private fun setUpView() {
        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomSheetAnimation)
        }
    }

    private fun setOnClick() {
        binding.apply {
            btnGoToSetting.setOnClickListener {
                onGoToSetting()
                dismiss()
            }
            btnDoNothing.setOnClickListener { dismiss() }
        }
    }
}