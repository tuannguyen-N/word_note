package com.example.wordnote.ui.fragment.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordnote.domain.model.state.CategoryState
import com.example.wordnote.domain.usecase.LocalCategoryUseCase
import com.example.wordnote.domain.model.Result
import com.example.wordnote.domain.usecase.LocalWordUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val localCategoryUseCase: LocalCategoryUseCase,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<CategoryUIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _categories = localCategoryUseCase.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(CategoryState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = _categories
        .flatMapLatest { categories ->
            flow {
                val previewMap =
                    localCategoryUseCase.getPreviewWords(categories.mapNotNull { it.id })
                emit(categories.map { category ->
                    category.copy(
                        previewWords = previewMap[category.id].orEmpty()
                    )
                })
            }
        }
        .map { list -> CategoryState(categories = list, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryState())

    fun onAction(action: CategoryAction) {
        when (action) {
            is CategoryAction.OnSaveCategory -> performSaveCategory(action.name, action.description)
            is CategoryAction.OnDeleteSelectedList -> performDeleteSelectedList(action.selectedIds)
            is CategoryAction.OnEditCategory -> performEditCategory(
                action.id,
                action.name,
                action.description
            )
            is CategoryAction.OnToggleFavorite -> performToggleFavorite(action.categoryId)
        }
    }

    private fun performToggleFavorite(id: Int){
        viewModelScope.launch {
            localCategoryUseCase.toggleFavorite(id)
        }
    }

    private fun performEditCategory(id: Int, name: String, description: String) {
        viewModelScope.launch {
            val result = localCategoryUseCase.updateCategory(id, name, description)
//            handleCategoryResult(result, name)
        }
    }

    private fun performDeleteSelectedList(selectedIds: List<Int>) {
        viewModelScope.launch {
            Log.e("123123", "performDeleteSelectedList: $selectedIds")
            localCategoryUseCase.deleteCategories(selectedIds)
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