package com.example.wordnote.ui.fragment.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wordnote.adapter.CategoryAdapter
import com.example.wordnote.data.AppDatabase
import com.example.wordnote.data.repository.CategoryRepository
import com.example.wordnote.databinding.FragmentCategoryBinding
import com.example.wordnote.domain.model.CategoryData
import com.example.wordnote.domain.usecase.LocalCategoryUseCase
import com.example.wordnote.ui.activity.spelling_bee.SpellingBeeActivity
import com.example.wordnote.ui.activity.word.WordActivity
import com.example.wordnote.ui.dialog.AddCategoryDialog
import com.example.wordnote.ui.dialog.ConfirmDeleteDialog
import com.example.wordnote.ui.dialog.EditCategoryDialog
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

    private val categoryAdapter = CategoryAdapter(
        onClickItem = { category ->
            WordActivity.goToActivity(requireContext(), category)
        },
        onDelete = { categoryId ->
            showConfirmDeleteDialog(categoryId)
        },
        onEdit = { category ->
            showEditCategoryDialog(category)
        },
        onPlay = {
            SpellingBeeActivity.goToActivity(requireContext(), it.id!!)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setOnClick()
        collectState()
        collectEvent()
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.state.collect {
                    categoryAdapter.setItemList(it.categories)
                    binding.viewNoData.visibility = if (it.categories.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun collectEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.uiEvent.collect {
                    when (it) {
                        is CategoryUIEvent.ScrollToExistCategory -> scrollToExistCategory(it.name)
                    }
                }
            }
        }
    }

    fun scrollToExistCategory(word: String) {
        val index =
            categoryAdapter.itemList.indexOfFirst { it.name.equals(word, ignoreCase = true) }
        if (index != -1) {
            binding.recyclerView.apply {
                smoothScrollToPosition(index)
                postDelayed({
                    val holder = this.findViewHolderForAdapterPosition(index)
                    holder?.itemView?.let { Utils.shakeView(it) }
                }, 400)
            }
        }
    }

    private fun showConfirmDeleteDialog(id: Int) {
        val dialog = ConfirmDeleteDialog(
            onConfirm = {
                categoryViewModel.onAction(CategoryAction.OnDeleteCategory(id))
            },
            onClose = {
                categoryAdapter.refresh()
            }
        )
        dialog.show(childFragmentManager, "confirm_delete_dialog")
    }

    private fun setOnClick() {
        binding.apply {
            btnAddCategory.setOnClickListener { showAddCategoryDialog() }
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

    private fun showEditCategoryDialog(category: CategoryData) {
        val dialog = EditCategoryDialog(
            category = category,
            onEnterClick = { categoryName, description ->
                categoryViewModel.onAction(
                    CategoryAction.OnEditCategory(
                        category.id!!,
                        categoryName,
                        description
                    )
                )
            }
        )
        dialog.show(childFragmentManager, "edit_category_dialog")
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}