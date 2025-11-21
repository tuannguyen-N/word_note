package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.databinding.DialogConfirmDeleteBinding

class ConfirmDeleteDialog(
    private val onConfirm: ()->Unit,
    private val onClose: ()->Unit
): BaseDialog<DialogConfirmDeleteBinding>(DialogConfirmDeleteBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClick()
    }

    private fun setOnClick(){
        binding.btnDelete.setOnClickListener {
            onConfirm()
            dismiss()
        }
        binding.btnClose.setOnClickListener {
            onClose()
            dismiss()
        }
    }
}