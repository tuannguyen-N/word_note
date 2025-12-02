package com.example.wordnote.adapter

import android.annotation.SuppressLint
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemMeaningBinding


data class AMeaningData(
    val partOfSpeech: String,
    val definition: String,
    val example: String?,
    val synonyms: List<String>?,
)

class MeaningAdapter(
    private val shouldShowExample: Boolean = true,
) : BaseAdapter<AMeaningData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_meaning

    @SuppressLint("SetTextI18n")
    override fun doBindViewHolder(
        view: View,
        item: AMeaningData,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemMeaningBinding.bind(view).apply {
            tvPartOfSpeech.text = item.partOfSpeech
            tvDefinition.text = item.definition

            when (tvPartOfSpeech.text) {
                "verb" -> tvPartOfSpeech.setBackgroundResource(R.drawable.bg_tv_verb)
                "interjection" -> tvPartOfSpeech.setBackgroundResource(R.drawable.bg_tv_interjection)
                "adjective" -> tvPartOfSpeech.setBackgroundResource(R.drawable.bg_tv_adj)
                "adverb" -> tvPartOfSpeech.setBackgroundResource(R.drawable.bg_tv_adv)
                else -> {
                    tvPartOfSpeech.setBackgroundResource(R.drawable.bg_tv_noun)
                }
            }

            if (item.example != null && shouldShowExample) {
                tvExample.visibility = View.VISIBLE
                tvExample.text = "\"${item.example}\""
            }

        }
    }
}