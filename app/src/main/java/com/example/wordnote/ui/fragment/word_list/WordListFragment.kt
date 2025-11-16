package com.example.wordnote.ui.fragment.word_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.WordAdapter
import com.example.wordnote.databinding.FragmentWordListBinding
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.LocalWordUseCase
import com.example.wordnote.ui.dialog.AddWordDialog
import com.example.wordnote.ui.dialog.DetailDefinitionDialog
import com.example.wordnote.ui.fragment.BaseFragment
import com.example.wordnote.util.SpeakingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordListFragment : BaseFragment<FragmentWordListBinding>(FragmentWordListBinding::inflate) {
    private val wordListViewModel: WordListViewModel by viewModels {
        WordListViewModelFactory(LocalWordUseCase(WordRepository()),SpeakingManager(requireContext()))
    }

    private val wordAdapter = WordAdapter(
        onAction = {
            wordListViewModel.onAction(WordListAction.OnOpenDetailWordDialog(it))
        },
        onClickTvWord = { word ->
            wordListViewModel.onAction(WordListAction.OnSpeakingWord(word))
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        listenUIEvent()
        listenLiveData()
        setOnClick()
    }

    private fun listenLiveData() {
        lifecycleScope.launch(Dispatchers.Main) {
            wordListViewModel.wordList.collect { listWord ->
                wordAdapter.setItemList(listWord)
            }
        }
    }

    private fun setOnClick() {
        binding.apply {
            btnAddWord.setOnClickListener {
                wordListViewModel.onAction(WordListAction.OnShowAddWordDialog)
            }
        }
    }

    private fun listenUIEvent() {
        lifecycleScope.launch(Dispatchers.Main) {
            wordListViewModel.uiEvent.collect { event ->
                when (event) {
                    is WordListUIEvent.ShowDetailWordDialog -> showDetailWordDialog(event.word)
                    is WordListUIEvent.ShowAddWordDialog -> showAddWordDialog()
                    is WordListUIEvent.ShowToast -> showToast(event.message)
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), "Speaking $message", Toast.LENGTH_SHORT).show()
    }

    private fun showAddWordDialog() {
        val dialog = AddWordDialog(
            onEnter = {
                wordListViewModel.onAction(WordListAction.OnAddNewWord(it))
            }
        )
        dialog.show(childFragmentManager, "AddWordDialog")
    }

    private fun setUpRecyclerView() {
        wordAdapter.setItemList()
        binding.recyclerView.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showDetailWordDialog(word: WordEntity) {
        val dialog = DetailDefinitionDialog(word)
        dialog.show(childFragmentManager, "DetailDefinitionDialog")
    }
}