package com.example.dolt2.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolt2.data.local.entity.CategoryEntity
import com.example.dolt2.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val uiState: StateFlow<CategoryUiState> = categoryRepository
        .getAllCategories()
        .map { categories ->
            CategoryUiState(
                categories = categories,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoryUiState(isLoading = true)
        )

    fun insertCategory(name: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            categoryRepository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}