package com.example.wordnote.adapter

import android.annotation.SuppressLint
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemChooseCategoryBinding
import com.example.wordnote.domain.model.CategoryData

class CurrentCategoryAdapter(
    private val currentCategory: Int,
    private val onClick: (Int) -> Unit
) : BaseAdapter<CategoryData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_choose_category

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun doBindViewHolder(
        view: View,
        item: CategoryData,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemChooseCategoryBinding.bind(view).apply {
            tvNameCategory.text = item.name
            tvWordNumber.text =
                (item.numberWordLevel1 + item.numberWordLevel2 + item.numberWordLevel3).toString() + " words"
            if (currentCategory == item.id) {
                tvNameCategory.setTextColor(R.color.icon)
                icon.setImageResource(R.drawable.icon_in_category)
            }

            root.setOnClickListener {
                onClick(item.id!!)
            }
        }
    }
}