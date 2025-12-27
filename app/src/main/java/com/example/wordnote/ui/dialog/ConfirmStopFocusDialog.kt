package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogConfirmStopFocusBinding
import com.example.wordnote.utils.loadGlideImage
import com.example.wordnote.utils.setSafeOnClickListener

class ConfirmStopFocusDialog(
    private val onDismiss: () -> Unit,
    private val onStopFocus: () -> Unit
) : BaseDialog<DialogConfirmStopFocusBinding>(
    DialogConfirmStopFocusBinding::inflate
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setSafeOnClickListener()
    }

    private fun setSafeOnClickListener() {
        binding.apply {
            btnClose.setSafeOnClickListener {
                onDismiss()
                dismiss()
            }

            btnStopFocus.setSafeOnClickListener {
                onStopFocus()
                dismiss()
            }
        }
    }

    private fun setUpView() {
        binding.image.loadGlideImage(R.drawable.image_are_usabt)
    }
}