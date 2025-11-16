package com.example.wordnote.adapter

import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordBinding
import com.example.wordnote.data.entities.WordEntity

class WordAdapter(
    private val onAction: (WordEntity) -> Unit,
    private val onClickTvWord: (String) -> Unit
) : BaseAdapter<WordEntity>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_word

    override fun doBindViewHolder(
        view: View,
        item: WordEntity,
        position: Int,
        holder: BaseViewHolder
    ) {
        val itemBinding = ItemWordBinding.bind(view)
        itemBinding.apply {
            tvWord.text = item.word
            tvDefinition.text = item.definition

            root.setOnClickListener {
                onAction(item)
            }
            tvWord.setOnClickListener {
                onClickTvWord(item.word)
            }
        }
    }
}