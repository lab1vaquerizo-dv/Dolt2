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
enum class TaskSort { BY_DATE, BY_PRIORITY }

data class TaskListUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val sort: TaskSort = TaskSort.BY_DATE,
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

    private val _sort = MutableStateFlow(TaskSort.BY_DATE)
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
        _filter, _searchQuery, _categories, _sort
    ) { filter, query, categories, sort ->
        object {
            val f = filter; val q = query; val c = categories; val s = sort
        }
    }.flatMapLatest { state ->
        val source = when {
            state.q.isNotBlank() -> taskRepository.searchTasks(state.q)
            state.f == TaskFilter.PENDING -> taskRepository.getPendingTasks()
            state.f == TaskFilter.COMPLETED -> taskRepository.getCompletedTasks()
            else -> taskRepository.getAllTasks()
        }
        source.map { tasks ->
            val sorted = when (state.s) {
                TaskSort.BY_PRIORITY -> tasks.sortedByDescending { it.priority }
                TaskSort.BY_DATE -> tasks
            }
            TaskListUiState(
                tasks = sorted,
                categories = state.c,
                filter = state.f,
                sort = state.s,
                searchQuery = state.q,
                isLoading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskListUiState(isLoading = true)
    )

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
        _searchQuery.value = ""
    }

    fun setSort(sort: TaskSort) {
        _sort.value = sort
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