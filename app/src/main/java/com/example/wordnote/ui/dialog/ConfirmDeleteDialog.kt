package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogConfirmDeleteBinding
import com.example.wordnote.utils.loadGlideImage

class ConfirmDeleteDialog(
    private val onConfirm: ()->Unit,
    private val onClose: ()->Unit
): BaseDialog<DialogConfirmDeleteBinding>(DialogConfirmDeleteBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
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

    private fun setUpView(){
        binding.image.loadGlideImage(R.drawable.image_are_usabt)
    }
}