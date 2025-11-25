package com.example.wordnote.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.MeaningAdapter
import com.example.wordnote.databinding.DialogDetailDefinitionBinding
import com.example.wordnote.data.mapper.toListMeaningData
import com.example.wordnote.domain.model.WordData

class DetailDefinitionDialog(
    private val word: WordData,
    private val onChangeNote: (WordData) -> Unit
) :
    BaseDialog<DialogDetailDefinitionBinding>(DialogDetailDefinitionBinding::inflate) {
    private val meaningAdapter: MeaningAdapter = MeaningAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpRecycler()
        setUpEditText()
        setOnClick()
    }

    private fun setOnClick() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setUpEditText() {
        binding.etNote.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.etNote.clearFocus()
                hideKeyboard(v)
                onChangeNote(word.copy(note = binding.etNote.text.toString()))
                true
            } else false
        }
    }

    private fun setUpView() {
        binding.apply {
            tvWord.text = word.word
            tvPhonetic.text = word.phonetic
            if (word.note.isNotEmpty()) etNote.setText(word.note)
        }
    }

    private fun setUpRecycler() {
        binding.rvMeaning.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = meaningAdapter
        }
        meaningAdapter.setItemList(word.toListMeaningData())
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}