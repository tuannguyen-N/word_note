package com.example.wordnote.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.example.wordnote.databinding.DialogAddQuiteTimeBinding

class AddQuiteHourDialog(
    private val onAddQuiteHour: (Long, Long) -> Unit,
) : BaseDialog<DialogAddQuiteTimeBinding>(DialogAddQuiteTimeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvStartHour.setupTimeInput(24)
            tvStartMinute.setupTimeInput(59)
            tvEndHour.setupTimeInput(24)
            tvEndMinute.setupTimeInput(59)
        }
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

        if (startMillis >= endMillis) {
            binding.tvError.visibility = View.VISIBLE
            return
        }
        
        onAddQuiteHour(startMillis, endMillis)
        dismiss()
    }

    private fun convertToMillis(hour: Int, minute: Int): Long =
        hour * 3600000L + minute * 60000L

    @SuppressLint("SetTextI18n")
    fun EditText.setupTimeInput(maxValue: Int) {
        isCursorVisible = false

        setOnClickListener { selectAll() }

        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) post { selectAll() }
        }

        customSelectionActionModeCallback = object : android.view.ActionMode.Callback {
            override fun onCreateActionMode(
                mode: android.view.ActionMode?,
                menu: android.view.Menu?
            ) = false

            override fun onPrepareActionMode(
                mode: android.view.ActionMode?,
                menu: android.view.Menu?
            ) = false

            override fun onActionItemClicked(
                mode: android.view.ActionMode?,
                item: android.view.MenuItem?
            ) = false

            override fun onDestroyActionMode(mode: android.view.ActionMode?) {}
        }

        addTextChangedListener(object : TextWatcher {
            private var editing = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (editing || s == null) return
                editing = true
                binding.tvError.visibility = View.GONE

                var digits = s.toString().filter { it.isDigit() }
                if (digits.length > 2) {
                    digits = digits.takeLast(2)
                }

                val number = digits.toIntOrNull() ?: 0
                val fixed = number.coerceIn(0, maxValue)
                val formatted = String.format("%02d", fixed)

                setText(formatted)
                setSelection(formatted.length)

                editing = false
            }
        })
    }
}