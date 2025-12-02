package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.DialogExistWordBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.utils.loadGlideImage

class ExistWordDialog(
    private val category: CategoryData,
    private val onGoToThisList: (CategoryData) -> Unit
) : BaseDialog<DialogExistWordBinding>(DialogExistWordBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setOnClick()
    }

    private fun setOnClick() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }

            btnGoToThisList.setOnClickListener {
                onGoToThisList(category)
                dismiss()
            }
        }
    }

    private fun setUpView() {
        binding.iv4.loadGlideImage(R.drawable._4)
        binding.tvExistWordAt.text =
            "The word is already in ${category.name.replaceFirstChar { it.uppercase() }}"
    }
}