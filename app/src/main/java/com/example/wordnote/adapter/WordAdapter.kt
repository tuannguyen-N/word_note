package com.example.wordnote.adapter

import android.util.Log
import android.view.View
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordBinding
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.domain.model.WordData

enum class Level(val level: Int) {
    LEVEL1(1),
    LEVEL2(2),
    LEVEL3(3),
    LEVEL4(4),
    LEVEL5(5)
}

class WordAdapter(
    private val onAction: (WordData) -> Unit,
    private val onDelete: (WordData) -> Unit,
    private val onClickTvWord: (String) -> Unit,
    private val onClickLevel: (WordData) -> Unit
) : BaseAdapter<WordData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_word

    override fun doBindViewHolder(
        view: View,
        item: WordData,
        position: Int,
        holder: BaseViewHolder
    ) {
        val itemBinding = ItemWordBinding.bind(view)
        val levelButtons = mapOf(
            itemBinding.btnLevel1 to Level.LEVEL1,
            itemBinding.btnLevel2 to Level.LEVEL2,
            itemBinding.btnLevel3 to Level.LEVEL3,
            itemBinding.btnLevel4 to Level.LEVEL4,
            itemBinding.btnLevel5 to Level.LEVEL5
        )

        // ----- Restore -----
        levelButtons.forEach { (btn, level) ->
            btn.alpha = if (item.level == level.level) 1f else 0.3f
        }

        itemBinding.apply {
            /*Click level*/
            levelButtons.forEach { (btn, level) ->
                btn.setOnClickListener {
                    levelButtons.keys.forEach { it.alpha = 0.3f }
                    btn.alpha = 1f
                    onClickLevel(item.copy(level = level.level))
                }
            }

            tvWord.text = item.word
            tvDefinition.text = item.meanings.first().definitions.first().definition

            btnDelete.visibility = View.GONE

            root.apply {
                setOnClickListener { onAction(item) }

                setOnLongClickListener {
                    btnDelete.visibility = View.VISIBLE
                    true
                }
            }

            btnDelete.setOnClickListener {
                btnDelete.visibility = View.GONE
                onDelete(item)
            }

            tvWord.setOnClickListener {
                onClickTvWord(item.word)
            }
        }
    }
}
