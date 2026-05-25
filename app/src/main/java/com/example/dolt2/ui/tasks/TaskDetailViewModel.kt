package com.example.dolt2.ui.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolt2.data.local.entity.CategoryEntity
import com.example.dolt2.data.local.entity.TaskEntity
import com.example.dolt2.data.repository.CategoryRepository
import com.example.dolt2.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskDetailUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Int = 1,
    val categoryId: Long? = null,
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val availableCategories: List<CategoryEntity> = emptyList(),
    val titleError: String? = null
)

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val taskId: Long? = savedStateHandle["taskId"]
    private val _uiState = MutableStateFlow(TaskDetailUiState(isLoading = taskId != null))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { cats ->
                _uiState.update { it.copy(availableCategories = cats) }
            }
        }
        if (taskId != null) {
            viewModelScope.launch {
                val task = taskRepository.getTaskById(taskId)
                if (task != null) {
                    _uiState.update {
                        it.copy(
                            taskId = task.id,
                            title = task.title,
                            description = task.description ?: "",
                            dueDate = task.dueDate,
                            priority = task.priority,
                            categoryId = task.categoryId,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, titleError = null) }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value) }

    fun onDueDateChange(epochMs: Long?) =
        _uiState.update { it.copy(dueDate = epochMs) }

    fun onPriorityChange(priority: Int) =
        _uiState.update { it.copy(priority = priority) }

    fun onCategoryChange(categoryId: Long?) =
        _uiState.update { it.copy(categoryId = categoryId) }

    fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "El título es obligatorio") }
            return
        }
        viewModelScope.launch {
            if (state.taskId == null) {
                taskRepository.insertTask(
                    TaskEntity(
                        title = state.title.trim(),
                        description = state.description.trim().ifBlank { null },
                        dueDate = state.dueDate,
                        priority = state.priority,
                        categoryId = state.categoryId
                    )
                )
            } else {
                val existing = taskRepository.getTaskById(state.taskId) ?: return@launch
                taskRepository.updateTask(
                    existing.copy(
                        title = state.title.trim(),
                        description = state.description.trim().ifBlank { null },
                        dueDate = state.dueDate,
                        priority = state.priority,
                        categoryId = state.categoryId
                    )
                )
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}