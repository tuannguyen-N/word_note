package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.databinding.DialogAddQuiteTimeBinding

class AddQuiteHourDialog(
    private val onAddQuiteHour: (Long, Long) -> Unit,
) : BaseDialog<DialogAddQuiteTimeBinding>(DialogAddQuiteTimeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnAdd.setOnClickListener {
                addQuiteHour()
            }
        }
    }

    private fun addQuiteHour() {
        val startHour = binding.tvStartHour.text.toString().toIntOrNull() ?: 0
        val startMinute = binding.tvStartMinute.text.toString().toIntOrNull() ?: 0
        val endHour = binding.tvEndHour.text.toString().toIntOrNull() ?: 0
        val endMinute = binding.tvEndMinute.text.toString().toIntOrNull() ?: 0

        val startMillis = convertToMillis(startHour, startMinute)
        val endMillis = convertToMillis(endHour, endMinute)

        onAddQuiteHour(startMillis, endMillis)
        dismiss()
    }

    private fun convertToMillis(hour: Int, minute: Int): Long =
        hour * 3600000L + minute * 60000L
}