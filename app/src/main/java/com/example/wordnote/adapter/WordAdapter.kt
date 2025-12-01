package com.example.wordnote.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import com.example.wordnote.R
import com.example.wordnote.databinding.ItemWordBinding
import com.example.wordnote.domain.model.WordData

class WordAdapter(
    private val onAction: (WordData) -> Unit,
    private val onSpeaking: (String) -> Unit,
    private val onStartStudying: (WordData) -> Unit,
    private val onStopStudying: (Int) -> Unit
) : BaseAdapter<WordData>() {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
//        val binding = ItemWordBinding.bind(view)
//        return WordViewHolder(binding)
//    }

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

        binding.containerForeground.setOnClickListener {
            onAction(item)
        }

        binding.btnSpeaking.setOnClickListener {
            onSpeaking(item.word)
        }
    }

    private fun ImageView.updateStudyIcon(isStudied: Boolean) {
        setImageResource(
            if (isStudied) R.drawable.icon_fire_clicked
            else R.drawable.ic_fire
        )
    }
}
