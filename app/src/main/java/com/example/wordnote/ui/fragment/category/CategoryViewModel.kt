package com.example.wordnote.ui.fragment.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.CategoryState
import com.example.wordnote.domain.usecase.LocalCategoryUseCase
import com.example.wordnote.util.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val localCategoryUseCase: LocalCategoryUseCase
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<CategoryUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val _categories = localCategoryUseCase.getCategoriesWithWordLevel()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(CategoryState())
    val state = combine(_state, _categories) { state, categories ->
        state.copy(
            categories = categories, isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryState())

    fun onAction(action: CategoryAction) {
        when (action) {
            is CategoryAction.OnSaveCategory -> performSaveCategory(action.name, action.description)
            is CategoryAction.OnDeleteCategory -> performDeleteCategory(action.id)
            is CategoryAction.OnEditCategory -> performEditCategory(
                action.id,
                action.name,
                action.description
            )
        }
    }

    private fun performEditCategory(id: Int, name: String, description: String) {
        viewModelScope.launch {
            val result = localCategoryUseCase.updateCategory(id, name, description)
            handleCategoryResult(result, name)
        }
    }

    private fun performDeleteCategory(id: Int) {
        viewModelScope.launch {
            localCategoryUseCase.deleteCategory(id)
        }
    }

    private fun performSaveCategory(name: String, description: String) {
        viewModelScope.launch {
            val result = localCategoryUseCase.insertCategory(name, description)
            handleCategoryResult(result, name)
        }
    }

    private fun handleCategoryResult(result: Result, name: String) {
        when (result) {
            is Result.AlreadyExists -> {
                sendUIEvent(CategoryUIEvent.ScrollToExistCategory(name))
            }

            is Result.AlreadyExistsInCategories -> {}
            is Result.Error -> {}
            is Result.NotFound -> {}
            is Result.Success -> {}
        }
    }

    fun sendUIEvent(event: CategoryUIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}