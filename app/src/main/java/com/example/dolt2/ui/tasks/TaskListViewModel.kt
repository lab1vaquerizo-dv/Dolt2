package com.example.dolt2.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dolt2.data.local.entity.CategoryEntity
import com.example.dolt2.data.local.entity.TaskEntity
import com.example.dolt2.data.repository.CategoryRepository
import com.example.dolt2.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, PENDING, COMPLETED }

data class TaskListUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { cats ->
                _categories.value = cats
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TaskListUiState> = combine(
        _filter,
        _searchQuery,
        _categories
    ) { filter, query, categories -> Triple(filter, query, categories) }
        .flatMapLatest { (filter, query, categories) ->
            val source = when {
                query.isNotBlank() -> taskRepository.searchTasks(query)
                filter == TaskFilter.PENDING -> taskRepository.getPendingTasks()
                filter == TaskFilter.COMPLETED -> taskRepository.getCompletedTasks()
                else -> taskRepository.getAllTasks()
            }
            source.map { tasks ->
                TaskListUiState(
                    tasks = tasks,
                    categories = categories,
                    filter = filter,
                    searchQuery = query,
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskListUiState(isLoading = true)
        )

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
        _searchQuery.value = ""
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTaskCompleted(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.toggleCompleted(task.id, task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}