package com.example.wordnote.adapter

import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemCategoryBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.item.CategoryItem

class CategoryAdapter(
    private val onClickItem: (CategoryData) -> Unit = {},
    private val onDelete: (Int) -> Unit,
    private val onEdit: (CategoryData) -> Unit,
    private val onPlay: (CategoryData) -> Unit
) : BaseAdapter<CategoryData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_category

    override fun doBindViewHolder(
        view: View,
        item: CategoryData,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemCategoryBinding.bind(view).apply {
            tvTitle.text = item.name.replaceFirstChar { it.uppercase() }
            tvDescription.text = item.description
            btnDelete.visibility = View.GONE
            tvWordLv1.text = item.numberWordLevel1.toString()
            tvWordLv2.text = item.numberWordLevel2.toString()
            tvWordLv3.text = item.numberWordLevel3.toString()

            root.setOnClickListener {
                onClickItem(item)
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
            btnPlay.setOnClickListener {
                onPlay(item)
            }

            btnPlay.visibility = if (
                item.numberWordLevel1 + item.numberWordLevel2 + item.numberWordLevel3 == 0
            ) View.GONE else View.VISIBLE
        }
    }

    fun refresh() {
        notifyDataSetChanged()
    }
}