package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.databinding.DialogAddCategoryBinding
import com.example.wordnote.domain.model.CategoryData

class EditCategoryDialog(
    private val category: CategoryData,
    private val onEnterClick: (String, String) -> Unit
) : BaseDialog<DialogAddCategoryBinding>(DialogAddCategoryBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setOnClick()
    }

    private fun setupView() {
        binding.apply {
            etCategory.setText(category.name)
            if (category.description.isNotEmpty()) etDescription.setText(category.description)
        }
    }

    private fun setOnClick() {
        binding.apply {
            cancelBtn.setOnClickListener { dismiss() }

            enterBtn.setOnClickListener {
                handleClickEnter()
            }
        }
    }

    private fun handleClickEnter() {
        val category = binding.etCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim().ifEmpty { "No description" }

        if (category.isNotEmpty()) {
            onEnterClick(category, description)
            dismiss()
        }
    }
}