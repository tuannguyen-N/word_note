package com.example.wordnote.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.wordnote.databinding.DialogAddWordBinding
import com.example.wordnote.domain.model.WordData

class AddWordDialog(
    private val onEnter: (String, Int) -> Unit,
) : BaseDialog<DialogAddWordBinding>(DialogAddWordBinding::inflate) {

    private var selectedLevel: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setOnClick()
    }

    private fun setUpView() {
        binding.etWord.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etWord, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setOnClick() {
        binding.apply {
            enterBtn.setOnClickListener {
                val word = etWord.text.toString().trim().lowercase()
                if (word.isEmpty()) {
                    etWord.error = "Please enter a word"
                    return@setOnClickListener
                }
                onEnter(word, selectedLevel)
                dismiss()
            }

            cancelBtn.setOnClickListener {
                dismiss()
            }

            btnLevel1.setOnClickListener { updateLevel(1) }
            btnLevel2.setOnClickListener { updateLevel(2) }
            btnLevel3.setOnClickListener { updateLevel(3) }
        }
    }

    private fun updateLevel(level: Int) {
        selectedLevel = level

        val levelButtons = mapOf(
            binding.btnLevel1 to 1,
            binding.btnLevel2 to 2,
            binding.btnLevel3 to 3,
        )

        levelButtons.forEach { (button, lvl) ->
            button.alpha = if (lvl == level) 1f else 0.1f
        }
    }
}
