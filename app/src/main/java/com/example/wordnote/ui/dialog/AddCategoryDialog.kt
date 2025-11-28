package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogAddCategoryBinding

class AddCategoryDialog(
    private val onEnter: (String, String) -> Unit
) : BaseDialog<DialogAddCategoryBinding>(DialogAddCategoryBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
    }

    private fun setOnClick() {
        binding.apply {
            cancelBtn.setOnClickListener { dismiss() }

            enterBtn.setOnClickListener {
                handleClickEnter()
            }

            etDescription.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                ) {
                    handleClickEnter()
                    true
                } else
                    false
            }
        }
    }

    private fun handleClickEnter() {
        val category = binding.etCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifEmpty { "No description" }

        if (category.isEmpty()) {
            handleEmptyCategory()
            return
        }

        onEnter(category, description)
        dismiss()
    }

    private fun handleEmptyCategory() {
        binding.etCategory.setBackgroundResource(R.drawable.background_et_border_error)
        binding.tvError.visibility = View.VISIBLE
    }
}