package com.example.wordnote.adapter

import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemCategoryBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.item.CategoryItem

class CategoryAdapter(
    private val onClickItem: (Int) -> Unit = {},
    private val onDelete: (Int) -> Unit,
    private val onEdit: (CategoryData) -> Unit
) : BaseAdapter<CategoryData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_category

    override fun doBindViewHolder(
        view: View,
        item: CategoryData,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemCategoryBinding.bind(view).apply {
            tvTitle.text = item.name
            tvDescription.text = item.description
            btnDelete.visibility = View.GONE
            tvWordLv1.text = item.numberWordLevel1.toString()
            tvWordLv2.text = item.numberWordLevel2.toString()
            tvWordLv3.text = item.numberWordLevel3.toString()

            root.setOnClickListener {
                onClickItem(item.id!!)
            }
            root.setOnLongClickListener {
                btnDelete.visibility = View.VISIBLE
                true
            }

            btnDelete.setOnClickListener {
                onDelete(item.id!!)
            }

            btnEdit.setOnClickListener {
                onEdit(item)
            }

        }
    }

    fun refresh(){
        notifyDataSetChanged()
    }
}