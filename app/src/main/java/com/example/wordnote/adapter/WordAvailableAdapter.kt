package com.example.wordnote.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordAvailableBinding
import com.example.wordnote.domain.model.WordData

class WordAvailableAdapter(
    private val onStartStudying: (Int) -> Unit,
    private val onStopStudying: (Int) -> Unit
) : BaseAdapter<WordData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_word_available

    override fun doBindViewHolder(
        view: View,
        item: WordData,
        position: Int,
        holder: BaseViewHolder
    ) {
        val binding = ItemWordAvailableBinding.bind(view)
        binding.setUpView(item)
        binding.setOnClick(item, position)
    }

    private fun ItemWordAvailableBinding.setUpView(item: WordData) {
        tvWord.text = item.word
        btnLearning.setImageResource(
            if (item.startStudiedTime > 0) R.drawable.icon_fire_clicked
            else R.drawable.ic_fire
        )
    }

    private fun ItemWordAvailableBinding.setOnClick(item: WordData, position: Int) {
        btnLearning.setOnClickListener {
            if (item.startStudiedTime <= 0) {
                onStartStudying(item.id!!)
                item.startStudiedTime = System.currentTimeMillis()
            } else {
                onStopStudying(item.id!!)
                item.startStudiedTime = 0
            }
            if (position != RecyclerView.NO_POSITION)
                notifyItemChanged(position)
        }
    }
}