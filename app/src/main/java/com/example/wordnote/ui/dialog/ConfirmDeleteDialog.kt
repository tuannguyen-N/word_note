package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogConfirmDeleteBinding
import com.example.wordnote.utils.loadGlideImage
import com.example.wordnote.utils.setSafeOnClickListener

class ConfirmDeleteDialog(
    private val onConfirm: ()->Unit,
    private val onClose: ()->Unit
): BaseDialog<DialogConfirmDeleteBinding>(DialogConfirmDeleteBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
    }

    private fun setOnClick(){
        binding.btnDelete.setSafeOnClickListener {
            onConfirm()
            dismiss()
        }
        binding.btnCancel.setSafeOnClickListener {
            onClose()
            dismiss()
        }
    }
}