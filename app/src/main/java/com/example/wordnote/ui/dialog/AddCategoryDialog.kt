package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogAddCategoryBinding
import com.example.wordnote.utils.loadGlideImage
import com.example.wordnote.utils.setSafeOnClickListener

class AddCategoryDialog(
    private val onEnter: (String, String) -> Unit
) : BaseDialog<DialogAddCategoryBinding>(DialogAddCategoryBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
        binding.ivCat.loadGlideImage(R.drawable.happy_cat)
    }

    private fun setOnClick() {
        binding.apply {
            btnCancel.setSafeOnClickListener { dismiss() }

            btnEnter.setSafeOnClickListener {
                handleClickEnter()
            }

            etDescription.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
                ) {
                    handleClickEnter()
                    true
                } else
                    false
            }

            etCategory.addTextChangedListener {
                etCategory.isActivated = false
                tvError.visibility = View.GONE
                ivValid.visibility = if (it?.isNotBlank() == true) View.VISIBLE else View.GONE
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
        binding.etCategory.isActivated = true
        binding.tvError.visibility = View.VISIBLE
    }
}