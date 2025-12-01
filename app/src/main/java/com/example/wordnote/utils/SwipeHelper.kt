package com.example.wordnote.utils

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.wordnote.R
import com.example.wordnote.adapter.BaseViewHolder
import com.example.wordnote.adapter.WordAdapter
import com.example.wordnote.databinding.ItemWordBinding
import kotlin.math.max
import kotlin.math.min

//interface SwipeViewHolder {
//    fun getForeground(): View
//}
//
//class WordViewHolder(
//    val binding: ItemWordBinding
//) : BaseViewHolder(binding.root), SwipeViewHolder {
//
//    override fun getForeground(): View = binding.containerForeground
//}
