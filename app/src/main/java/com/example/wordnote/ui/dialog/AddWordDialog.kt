package com.example.wordnote.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogAddWordBinding
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.utils.loadGlideImage

class AddWordDialog(
    private val onEnter: (String) -> Unit,
) : BaseDialog<DialogAddWordBinding>(DialogAddWordBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setOnClick()
    }

    private fun setUpView() {
        binding.ivCat.loadGlideImage(R.drawable.happy_cat)
        binding.etWord.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etWord, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setOnClick() {
        binding.apply {
            enterBtn.setOnClickListener { handleClickEnter() }

            cancelBtn.setOnClickListener { dismiss() }

            etWord.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                ) {
                    handleClickEnter()
                    true
                } else false
            }
        }
    }

    private fun handleClickEnter() {
        val word = binding.etWord.text.toString().trim().lowercase()
        if (word.isEmpty())
            handleEmptyWord()
        else {
            onEnter(word)
            dismiss()
        }
    }

    private fun handleEmptyWord() {
        binding.etWord.setBackgroundResource(R.drawable.background_et_border_error)
        binding.tvError.visibility = View.VISIBLE
    }

}
