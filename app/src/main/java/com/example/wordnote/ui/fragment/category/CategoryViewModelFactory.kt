package com.example.wordnote.ui.fragment.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wordnote.domain.usecase.LocalCategoryUseCase

class CategoryViewModelFactory(
    private val localCategoryUseCase: LocalCategoryUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(
            localCategoryUseCase
        ) as T
    }
}