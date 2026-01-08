package com.example.wordnote.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordBinding
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.utils.setSafeOnClickListener

class WordAdapter(
    private val onAction: (WordData) -> Unit,
    private val onSpeaking: (String) -> Unit,
    private val onStartStudying: (WordData) -> Unit,
    private val onStopStudying: (Int) -> Unit,
    private val onDeleteWord: (Int) -> Unit,
    private val onChangeCategory: (Int) -> Unit
) : BaseAdapter<WordData>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_word

    @SuppressLint("NotifyDataSetChanged")
    override fun doBindViewHolder(
        view: View, item: WordData, position: Int, holder: BaseViewHolder
    ) {
        val binding = ItemWordBinding.bind(view)
        val id = item.id!!

        binding.tvWord.text = item.word
        binding.tvPhonetic.text = item.phonetic
        binding.tvNote.text = item.note.ifEmpty { view.context.getString(R.string.note) }
        binding.btnStartStudying.updateStudyIcon(item.startStudiedTime > 0)
//        binding.btnStartStudying.isEnabled = item.level < 2
        binding.ivLevel.setImageResource(
            when (item.level) {
                1 -> R.drawable.vocab_level_1
                2 -> R.drawable.vocab_level_2
                3 -> R.drawable.vocab_level_3
                else -> R.drawable.vocab_level_4
            }
        )
        binding.btnStartStudying.setImageResource(if (item.startStudiedTime <=0) R.drawable.icon_studying_no_click else R.drawable.icon_studying)

        binding.btnStartStudying.setSafeOnClickListener {
            if (item.startStudiedTime <= 0) onStartStudying(item)
            else onStopStudying(id)
            notifyItemChanged(position, false)
        }

        binding.containerForeground.setSafeOnClickListener {
            onAction(item)
        }

        binding.btnSpeaking.setSafeOnClickListener {
            onSpeaking(item.word)
        }

        binding.btnDelete.setSafeOnClickListener {
            onDeleteWord(item.id)
        }

        binding.btnChangeCategory.setSafeOnClickListener {
            onChangeCategory(item.id)
        }
    }

    private fun ImageView.updateStudyIcon(isStudied: Boolean) {
        setImageResource(
            if (isStudied) R.drawable.icon_studying
            else R.drawable.icon_studying_no_click
        )
    }
}
