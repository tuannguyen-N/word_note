package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogConfirmStopFocusBinding
import com.example.wordnote.utils.loadGlideImage

class ConfirmStopFocusDialog(
    private val onDismiss: () -> Unit,
    private val onStopFocus: () -> Unit
) : BaseDialog<DialogConfirmStopFocusBinding>(
    DialogConfirmStopFocusBinding::inflate
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.apply {
            btnClose.setOnClickListener {
                onDismiss()
                dismiss()
            }

            btnStopFocus.setOnClickListener {
                onStopFocus()
                dismiss()
            }
        }
    }

    private fun setUpView() {
        binding.image.loadGlideImage(R.drawable.image_are_usabt)
    }
}