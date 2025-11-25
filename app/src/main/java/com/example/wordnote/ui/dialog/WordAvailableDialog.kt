package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.WordAvailableAdapter
import com.example.wordnote.databinding.DialogWordAvailableBinding
import com.example.wordnote.domain.model.WordData

class WordAvailableDialog(
    private val listItem: List<WordData>,
    private val onStartStudying: (Int) -> Unit,
    private val onStopStudying: (Int) -> Unit
) : BaseDialog<DialogWordAvailableBinding>(DialogWordAvailableBinding::inflate) {
    private val wordAvailableAdapter = WordAvailableAdapter(
        onStartStudying = {
            onStartStudying(it)
        },
        onStopStudying = {
            onStopStudying(it)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setOnClick()
    }

    private fun setupAdapter() {
        wordAvailableAdapter.setItemList(listItem)
        binding.recyclerView.apply {
            adapter = wordAvailableAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setOnClick() {
        binding.btnClose.setOnClickListener { dismiss() }
    }

}