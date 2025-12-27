package com.example.wordnote.adapter

import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemCategoryAddBinding
import com.example.wordnote.databinding.ItemCategoryBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.model.item.CategoryItem
import com.example.wordnote.utils.color
import com.example.wordnote.utils.setSafeOnClickListener
import com.example.wordnote.utils.toStyle

class CategoryAdapter(
    private val onClickItem: (CategoryData) -> Unit = {},
    private val onPlay: (CategoryData) -> Unit,
    private val onAddClick: () -> Unit,
    private val onDeleteMode: (Boolean) -> Unit
) : BaseAdapter<CategoryItem>() {
    private var deleteMode = false
    private val selectedIds = mutableListOf<Int>()

    override fun doGetViewType(position: Int): Int {
        return when (mItemList[position]) {
            is CategoryItem.Data -> R.layout.item_category
            is CategoryItem.Add -> R.layout.item_category_add
        }
    }

    override fun doBindViewHolder(
        view: View, item: CategoryItem, position: Int, holder: BaseViewHolder
    ) {
        when (item) {
            is CategoryItem.Data -> bindCategory(view, item.data)
            is CategoryItem.Add -> bindAdd(view)
        }
    }

    private fun bindCategory(view: View, item: CategoryData) {
        val binding = ItemCategoryBinding.bind(view)
        val style = item.color.toStyle()

        binding.apply {
            tvNameCategory.text = item.name
            tvDescription.text = item.description
            tvDescription.visibility = if (item.description.isEmpty()) View.GONE else View.VISIBLE

            cardView.setCardBackgroundColor(view.color(style.background))
            tvDescription.setTextColor(view.color(style.textColor))

            if (deleteMode) {
                ivSelected.visibility = View.VISIBLE
                ivSelected.setBackgroundResource(if (selectedIds.contains(item.id)) R.drawable.icon_selected else R.drawable.icon_unselected)
            } else {
                ivSelected.visibility = View.GONE
            }

            root.setOnClickListener {
                if (deleteMode) {
                    toggleSelect(item)
                } else {
                    onClickItem(item)
                }
            }

            root.setOnLongClickListener {
                if (!deleteMode) {
                    changeToDeleteMode(true)
                    toggleSelect(item)
                }
                true
            }

            tvNameCategory.setSafeOnClickListener {
                if (!deleteMode) onPlay(item)
            }
        }
    }

    private fun toggleSelect(item: CategoryData) {
        if (selectedIds.contains(item.id)) {
            selectedIds.remove(item.id)
        } else {
            selectedIds.add(item.id!!)
        }

        val index = mItemList.indexOfFirst {
            it is CategoryItem.Data && it.data.id == item.id
        }

        if (index != -1) notifyItemChanged(index)
    }

    fun changeToDeleteMode(enable: Boolean) {
        deleteMode = enable
        if (!enable) {
            selectedIds.clear()
        }
        onDeleteMode(deleteMode)
        notifyDataSetChanged()
    }

    private fun bindAdd(view: View) {
        val binding = ItemCategoryAddBinding.bind(view)
        binding.root.setSafeOnClickListener {
            onAddClick()
        }
        binding.root.visibility = if (deleteMode) View.GONE else View.VISIBLE
    }

    fun submitCategories(categories: List<CategoryData>) {
        val list = mutableListOf<CategoryItem>()
        list.addAll(categories.map { CategoryItem.Data(it) })
        list.add(CategoryItem.Add)
        setItemList(list)
    }

    fun getSelectedIds(): List<Int> = selectedIds.toList()

    fun getDeleteMode(): Boolean = deleteMode

    fun resetState(){
        selectedIds.clear()
        deleteMode = false
        notifyDataSetChanged()
    }
}