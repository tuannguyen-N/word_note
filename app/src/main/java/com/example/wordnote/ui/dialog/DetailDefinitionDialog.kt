package com.example.wordnote.ui.dialog

import android.os.Bundle
import android.view.View
import com.example.wordnote.databinding.DialogDetailDefinitionBinding
import com.example.wordnote.data.entities.WordEntity


class DetailDefinitionDialog(
    private val word: WordEntity
) :
    BaseDialog<DialogDetailDefinitionBinding>(DialogDetailDefinitionBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpView()
    }

    private fun setUpView(){
        binding.apply {
            tvWord.text = word.word
            tvNote.text = word.definition
        }
    }
}