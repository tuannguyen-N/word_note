package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.CurrentCategoryAdapter
import com.example.wordnote.databinding.BottomSheetCurrentCategoryBinding
import com.example.wordnote.domain.model.CategoryData

class CurrentCategoryBottomSheet(
    private val currentCategory: Int,
    private val categories: List<CategoryData>,
    private val onClickItem: (Int) -> Unit
) : BaseDialog<BottomSheetCurrentCategoryBinding>(
    BottomSheetCurrentCategoryBinding::inflate
) {
    private val currentCategoryAdapter = CurrentCategoryAdapter(
        currentCategory = currentCategory,
        onClick = { id ->
            onClickItem(id)
            dismiss()
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
        }
    }

    private fun setUpView() {
        binding.apply {
            currentCategoryAdapter.setItemList(categories)
            recyclerView.adapter = currentCategoryAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}