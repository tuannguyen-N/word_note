package com.example.wordnote.adapter

import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemMeaningBinding


data class AMeaningData(
    val partOfSpeech: String,
    val definition: String,
    val example: String?,
    val synonyms: List<String>?,
)

class MeaningAdapter : BaseAdapter<AMeaningData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_meaning

    override fun doBindViewHolder(
        view: View,
        item: AMeaningData,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemMeaningBinding.bind(view).apply {
            tvPartOfSpeech.text = "(${item.partOfSpeech})"
            tvDefinition.text = item.definition
            if (item.example != null) {
                tvExample.visibility = View.VISIBLE
                tvExample.text = "\"${item.example}\""
            }
        }
    }
}