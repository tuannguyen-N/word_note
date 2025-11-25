package com.example.wordnote.adapter

import android.view.View
import android.widget.ImageView
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordBinding
import com.example.wordnote.domain.model.WordData

class WordAdapter(
    private val onAction: (WordData) -> Unit,
    private val onDelete: (WordData) -> Unit,
    private val onClickTvWord: (String) -> Unit,
    private val onStartStudying: (WordData) -> Unit,
    private val onStopStudying: (Int) -> Unit
) : BaseAdapter<WordData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_word

    override fun doBindViewHolder(
        view: View, item: WordData, position: Int, holder: BaseViewHolder
    ) {
        val binding = ItemWordBinding.bind(view)
        binding.bindWord(item)
        binding.bindView(item)
        binding.bindActions(item)
        binding.bindStudied(item, position)
    }

    private fun ItemWordBinding.bindWord(item: WordData) {
        tvWord.text = item.word
        tvPhonetic.text = item.phonetic

        tvWord.setOnClickListener {
            onClickTvWord(item.word)
        }
    }

    private fun ItemWordBinding.bindView(item: WordData) {
        if (item.note.isNotEmpty())
            tvNote.text = item.note
        else
            tvNote.setText(R.string.note)
        btnLevel1.alpha = if (item.level == 1) 1f else 0.2f
        btnLevel2.alpha = if (item.level == 2) 1f else 0.2f
        btnLevel3.alpha = if (item.level == 3) 1f else 0.2f
    }

    private fun ItemWordBinding.bindActions(item: WordData) {
        btnDelete.visibility = View.GONE

        root.setOnClickListener { onAction(item) }

        root.setOnLongClickListener {
            btnDelete.visibility = View.VISIBLE
            true
        }

        btnDelete.setOnClickListener {
            btnDelete.visibility = View.GONE
            onDelete(item)
        }
    }

    private fun ItemWordBinding.bindStudied(item: WordData, position: Int) {
        btnStartStudying.updateStudyIcon(item.startStudiedTime > 0)
        if (item.level < 2) {
            btnStartStudying.isEnabled = true
            btnStartStudying.setOnClickListener {
                if (item.startStudiedTime <= 0) onStartStudying(item)
                else onStopStudying(item.id!!)
                notifyItemChanged(position)
            }
        } else {
            btnStartStudying.isEnabled = false
            btnStartStudying.setOnClickListener(null)
        }
    }

    private fun ImageView.updateStudyIcon(isStudied: Boolean) {
        setImageResource(
            if (isStudied) R.drawable.icon_fire_clicked
            else R.drawable.ic_fire
        )
    }
}
