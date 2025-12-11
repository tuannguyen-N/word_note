package com.example.wordnote.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.graphics.toColorInt
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemCategoryInWordsBinding
import com.example.wordnote.domain.model.CategoryData

class CategoryInWordsAdapter(
    private val currentCategoryId: Int
) : BaseAdapter<CategoryData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_category_in_words

    @SuppressLint("SetTextI18n")
    override fun doBindViewHolder(
        view: View,
        item: CategoryData,
        position: Int,
        holder: BaseViewHolder
    ) {
        val binding = ItemCategoryInWordsBinding.bind(view)
        binding.apply {
            categoryName.text = item.name
            tvWordNumber.text =
                (item.numberWordLevel1 + item.numberWordLevel2 + item.numberWordLevel3).toString() + " Words"
        }

        if (item.id == currentCategoryId) {
            binding.apply {
                cardView.setCardBackgroundColor("#FFF0AA".toColorInt())
                categoryName.setTextColor(R.color.black)
            }
        } else {
            binding.apply {
                cardView.setCardBackgroundColor(R.color.black)
                categoryName.setTextColor(R.color.white)
            }
        }

        binding.apply {
            btnPlay.setOnClickListener {

            }
        }

    }
}