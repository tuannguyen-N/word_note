package com.example.wordnote.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.wordnote.databinding.DialogAddWordBinding

class AddWordDialog(
    private val onEnter: (word: String) -> Unit,
) : BaseDialog<DialogAddWordBinding>(DialogAddWordBinding::inflate) {
    private var word: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setOnClick()
    }

    private fun setUpView(){
        binding.etWord.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etWord, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setOnClick() {
        binding.apply {

            enterBtn.setOnClickListener {
                word = etWord.text.toString().trim().lowercase()
                if (word!!.isNotEmpty()) {
                    onEnter(word!!)
                    dismiss()
                } else
                    etWord.error = "Please enter a word"
            }
            cancelBtn.setOnClickListener {
                dismiss()
            }
        }
    }
}