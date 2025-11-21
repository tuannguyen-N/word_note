package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.databinding.DialogAddCategoryBinding

class AddCategoryDialog(
    private val onEnterClick: (String, String) -> Unit
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
        }
    }

    private fun handleClickEnter(){
        val category = binding.etCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifEmpty { "No description" }

        if (category.isNotEmpty()) {
            onEnterClick(category, description)
            dismiss()
        }
    }
}