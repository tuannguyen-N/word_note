package com.example.wordnote.ui.fragment.word_list

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.WordAdapter
import com.example.wordnote.data.WordDatabase
import com.example.wordnote.data.api.RetrofitInstance
import com.example.wordnote.data.dao.WordDao
import com.example.wordnote.databinding.FragmentWordListBinding
import com.example.wordnote.data.entities.WordEntity
import com.example.wordnote.data.repository.WordRepository
import com.example.wordnote.domain.usecase.LocalWordUseCase
import com.example.wordnote.domain.model.WordData
import com.example.wordnote.ui.dialog.AddWordDialog
import com.example.wordnote.ui.dialog.DetailDefinitionDialog
import com.example.wordnote.ui.fragment.BaseFragment
import com.example.wordnote.util.SortType
import com.example.wordnote.util.SpeakingManager
import com.example.wordnote.util.shakeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import com.example.wordnote.alarm.AlarmScheduler
import com.example.wordnote.domain.usecase.ScheduleWordUseCase

class WordListFragment : BaseFragment<FragmentWordListBinding>(FragmentWordListBinding::inflate) {
    private val wordListViewModel: WordListViewModel by viewModels {
        WordListViewModelFactory(
            LocalWordUseCase(
                WordRepository(
                    WordDatabase.getInstance(requireContext()).dao,
                    RetrofitInstance.api
                )
            ),
            SpeakingManager(requireContext()),
            ScheduleWordUseCase(AlarmScheduler(requireContext()))
        )
    }

    private val wordAdapter = WordAdapter(
        onAction = {
            wordListViewModel.onAction(WordListAction.OnOpenDetailWordDialog(it))
        },
        onDelete = {
            wordListViewModel.onAction(WordListAction.OnDeleteWord(it))
        },
        onClickTvWord = { word ->
            wordListViewModel.onAction(WordListAction.OnSpeakingWord(word))
        },
        onClickLevel = {
            wordListViewModel.onAction(WordListAction.OnUpdateLevel(it))
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setOnClick()
        collectState()
        collectUIEvent()
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            adapter = wordAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setOnClick() {
        binding.apply {
            btnAddWord.setOnClickListener {
                wordListViewModel.onAction(WordListAction.OnShowAddWordDialog)
            }
            btnSort.setOnClickListener {
                levelContainer.root.visibility =
                    if (levelContainer.root.isVisible) View.GONE else View.VISIBLE
            }
            levelContainer.apply {
                btnLevel1.setOnClickListener {
                    wordListViewModel.onAction(WordListAction.OnSortWords(SortType.LEVEL(1)))
                }
                btnLevel2.setOnClickListener {
                    wordListViewModel.onAction(WordListAction.OnSortWords(SortType.LEVEL(2)))
                }
                btnLevel3.setOnClickListener {
                    wordListViewModel.onAction(WordListAction.OnSortWords(SortType.LEVEL(3)))
                }
            }
        }
    }

    private fun collectState() {
        /* shouldn't use that because it can cause memory leak (when fragment is destroyed, the job is not cancel)*/
//        lifecycleScope.launch(Dispatchers.Main) { ... }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordListViewModel.state.collect { wordState ->
                    loadingUI(wordState.isLoading)
                    binding.viewNodata.visibility =
                        if (wordState.words.isEmpty()) View.VISIBLE else View.GONE
                    wordAdapter.setItemList(wordState.words)
                    onSelectedLevel(wordState.selectedLevel)
                }
            }
        }
    }

    private fun onSelectedLevel(selectedLevel: Int?) {
        val levelButtons = mapOf(
            binding.levelContainer.btnLevel1 to 1,
            binding.levelContainer.btnLevel2 to 2,
            binding.levelContainer.btnLevel3 to 3,
        )
        levelButtons.forEach { (button, level) ->
            button.alpha = if (selectedLevel == level) 1f else 0.3f
        }
    }

    private fun loadingUI(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun collectUIEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordListViewModel.uiEvent.collect { event ->
                    when (event) {
                        WordListUIEvent.ShowAddWordDialog -> showAddWordDialog()
                        is WordListUIEvent.ShowDetailWordDialog -> showDetailWordDialog(event.word)
                        is WordListUIEvent.ShowToast -> showToast(event.message)
                        is WordListUIEvent.HideLevelContainer -> hideLevelContainer()
                        is WordListUIEvent.ScrollToExistWord -> scrollToExistWord(event.word)
                    }
                }
            }
        }
    }

    fun scrollToExistWord(word: String) {
        binding.recyclerView.post {
            val index =
                wordAdapter.itemList.indexOfFirst { it.word.equals(word, ignoreCase = true) }
            if (index != -1) {
                binding.recyclerView.smoothScrollToPosition(index)

                binding.recyclerView.postDelayed({
                    val holder = binding.recyclerView.findViewHolderForAdapterPosition(index)
                    holder?.itemView?.let { shakeView(it) }
                }, 400)
            }
        }
    }

    private fun hideLevelContainer() {
        binding.levelContainer.root.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddWordDialog() {
        val dialog = AddWordDialog(
            onEnter = { word, level ->
                wordListViewModel.onAction(WordListAction.OnSaveWord(word, level))
            }
        )
        dialog.show(childFragmentManager, "AddWordDialog")
    }

    private fun showDetailWordDialog(word: WordData) {
        val dialog = DetailDefinitionDialog(word) { newWord ->
            wordListViewModel.onAction(WordListAction.OnUpdateNote(newWord))
        }
        dialog.show(childFragmentManager, "DetailDefinitionDialog")
    }
}