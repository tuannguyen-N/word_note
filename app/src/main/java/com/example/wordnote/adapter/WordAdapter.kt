package com.example.wordnote.adapter

import android.view.View
import android.widget.ImageView
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordBinding
import com.example.wordnote.domain.model.WordData

class WordAdapter(
    private val onAction: (WordData) -> Unit,
    private val onClickTvWord: (String) -> Unit,
    private val onStartStudying: (WordData) -> Unit,
    private val onStopStudying: (Int) -> Unit,
    private val onSelectedMode: (Boolean) -> Unit,
) : BaseAdapter<WordData>() {
    private val tickedIds = mutableSetOf<Int>()
    private var selectionMode = false

    override fun doGetViewType(position: Int): Int = R.layout.item_word

    override fun doBindViewHolder(
        view: View, item: WordData, position: Int, holder: BaseViewHolder
    ) {
        val binding = ItemWordBinding.bind(view)
        val id = item.id!!

        binding.btnTick.visibility = if (selectionMode) View.VISIBLE else View.GONE
        binding.btnTick.setImageResource(
            if (id in tickedIds) R.drawable.icon_ticked
            else R.drawable.icon_unticked
        )

        binding.tvWord.text = item.word
        binding.tvPhonetic.text = item.phonetic
        binding.tvNote.text = item.note.ifEmpty { view.context.getString(R.string.note) }

        binding.btnLevel1.alpha = if (item.level == 1) 1f else 0.2f
        binding.btnLevel2.alpha = if (item.level == 2) 1f else 0.2f
        binding.btnLevel3.alpha = if (item.level == 3) 1f else 0.2f

        binding.btnStartStudying.updateStudyIcon(item.startStudiedTime > 0)
        binding.btnStartStudying.isEnabled = item.level < 2
        binding.btnStartStudying.setOnClickListener {
            if (item.startStudiedTime <= 0) onStartStudying(item)
            else onStopStudying(id)
            notifyItemChanged(position, false)
        }

        binding.container.setOnLongClickListener {
            if (!selectionMode) {
                selectionMode = true
                tickedIds.add(id)
                onSelectedMode(selectionMode)
                notifyDataSetChanged()
            }
            true
        }

        binding.container.setOnClickListener {
            if (selectionMode) toggleItem(id, position)
            else onAction(item)
        }

        binding.btnTick.setOnClickListener {
            toggleItem(id, position)
        }

        binding.tvWord.setOnClickListener {
            if (!selectionMode) onClickTvWord(item.word)
        }
    }

    private fun toggleItem(id: Int, position: Int) {
        if (id in tickedIds) tickedIds.remove(id)
        else tickedIds.add(id)

        if (tickedIds.isEmpty()) {
            onDone()
        } else {
            notifyItemChanged(position, false)
        }
    }

    fun getTickedItem(): Set<Int> = tickedIds

    fun onDone(){
        selectionMode = false
        tickedIds.clear()
        onSelectedMode(false)
        notifyDataSetChanged()
    }

    private fun ImageView.updateStudyIcon(isStudied: Boolean) {
        setImageResource(
            if (isStudied) R.drawable.icon_fire_clicked
            else R.drawable.ic_fire
        )
    }
}
