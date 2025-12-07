package com.example.wordnote.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.wordnote.R
import com.example.wordnote.data.entities.QuiteHourEntity
import com.example.wordnote.databinding.ItemQuiteHourBinding

class QuiteHourAdapter(
    private val onDeleteQuiteHour: (Int) -> Unit
) : BaseAdapter<QuiteHourEntity>() {
    override fun doGetViewType(position: Int): Int = R.layout.item_quite_hour

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doBindViewHolder(
        view: View,
        item: QuiteHourEntity,
        position: Int,
        holder: BaseViewHolder
    ) {
        ItemQuiteHourBinding.bind(view).apply {
            tvQuiteTime.text = "${item.startTime.toHourMinute()} - ${item.endTime.toHourMinute()}"
            btnRemoveQuiteTime.setOnClickListener { onDeleteQuiteHour(item.id) }
        }
    }

    @SuppressLint("DefaultLocale")
    fun Long.toHourMinute(): String {
        val hour = (this / 3600000L).toInt()
        val minute = ((this % 3600000L) / 60000L).toInt()
        return String.format("%02d:%02d", hour, minute)
    }
}