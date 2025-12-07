package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.databinding.DialogEndSpellingBeeBinding

class EndSpellingBeeDialog(
    private val onHome: () -> Unit,
    private val onAgain: () -> Unit
) : BaseDialog<DialogEndSpellingBeeBinding>(DialogEndSpellingBeeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnHome.setOnClickListener {
                onHome()
                dismiss()
            }

            btnAgain.setOnClickListener {
                onAgain()
                dismiss()
            }
        }
    }
}