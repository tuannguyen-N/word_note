package com.example.wordnote.ui.fragment.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wordnote.adapter.CategoryAdapter
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.repository.CategoryRepository
import com.example.wordnote.databinding.FragmentCategoryBinding
import com.example.wordnote.domain.model.item.CategoryItem
import com.example.wordnote.domain.usecase.LocalCategoryUseCase
import com.example.wordnote.ui.activity.main.MainAction
import com.example.wordnote.ui.activity.main.MainViewModel
import com.example.wordnote.ui.activity.main.MainViewUIEvent
import com.example.wordnote.ui.activity.spelling_bee.SpellingBeeActivity
import com.example.wordnote.ui.activity.word.WordActivity
import com.example.wordnote.ui.dialog.AddCategoryDialog
import com.example.wordnote.ui.dialog.ConfirmDeleteDialog
import com.example.wordnote.ui.fragment.BaseFragment
import com.example.wordnote.utils.Utils
import kotlinx.coroutines.launch

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModelFactory(
            LocalCategoryUseCase(
                CategoryRepository(
                    AppDatabase.getInstance(requireContext()).categoryDao
                )
            )
        )
    }

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var backCallback: OnBackPressedCallback

    private val categoryAdapter = CategoryAdapter(
        onClickItem = { category ->
            WordActivity.goToActivity(requireContext(), category)
        },
        onPlay = {
            SpellingBeeActivity.goToActivity(requireContext(), it.id!!)
        },
        onAddClick = {
            showAddCategoryDialog()
        },
        onDeleteMode = { isDeleteMode ->
            handleDeleteMode(isDeleteMode)
        }
    )

    private fun handleDeleteMode(isDeleteMode: Boolean) {
        backCallback.isEnabled = isDeleteMode
        onMainAction(MainAction.OnChangeDeleteMode(isDeleteMode))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setOnClick()
        collectState()
        collectEvent()
        collectMainUIEvent()
        setupBackCallback()
    }

    private fun collectMainUIEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiEvent.collect { uIEvent ->
                    when (uIEvent) {
                        is MainViewUIEvent.RequestDelete -> {
                            showConfirmDeleteDialog()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showConfirmDeleteDialog() {
        val dialog = ConfirmDeleteDialog(
            onConfirm = {
                onDeleteSelectedList()
            },
            onClose = {
                categoryAdapter.changeToDeleteMode(false)
            }
        )
        dialog.show(childFragmentManager, "confirm_delete_dialog")
    }

    private fun onDeleteSelectedList() {
        val selectedIds = categoryAdapter.getSelectedIds()
        categoryViewModel.onAction(CategoryAction.OnDeleteSelectedList(selectedIds))
        categoryAdapter.changeToDeleteMode(false)
    }

    private fun collectState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.state.collect {
                    categoryAdapter.submitCategories(it.categories)
                }
            }
        }
    }

    private fun collectEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.uiEvent.collect {
                    when (it) {
                        is CategoryUIEvent.ScrollToExistCategory -> scrollToExistCategory(it.name)
                    }
                }
            }
        }
    }

    private fun scrollToExistCategory(word: String) {
        val index = categoryAdapter.itemList.indexOfFirst {
            it is CategoryItem.Data &&
                    it.data.name.equals(word, ignoreCase = true)
        }

        if (index == -1) return

        binding.recyclerView.apply {
            smoothScrollToPosition(index)

            postDelayed({
                val holder = findViewHolderForAdapterPosition(index)
                holder?.itemView?.let { view ->
                    Utils.shakeView(view)
                }
            }, 300)
        }
    }

    private fun setOnClick() {
        binding.apply {
        }
    }

    private fun showAddCategoryDialog() {
        val dialog = AddCategoryDialog(
            onEnter = { categoryName, description ->
                categoryViewModel.onAction(CategoryAction.OnSaveCategory(categoryName, description))
            }
        )
        dialog.show(childFragmentManager, "add_category_dialog")
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = categoryAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun setupBackCallback() {
        backCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                exitDeleteMode()
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backCallback)
    }

    private fun exitDeleteMode() {
        categoryAdapter.changeToDeleteMode(false)
    }

    private fun onMainAction(action: MainAction) {
        mainViewModel.onAction(action)
    }
}