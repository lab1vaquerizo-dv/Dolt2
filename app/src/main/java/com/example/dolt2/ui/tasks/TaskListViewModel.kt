package com.example.dolt2.ui.tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.dolt2.data.local.entity.TaskEntity
import com.example.dolt2.data.repository.TaskRepository
import com.example.dolt2.notifications.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

enum class TaskFilter { ALL, PENDING, COMPLETED }

data class TaskListUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TaskListUiState> = combine(
        _filter,
        _searchQuery
    ) { filter, query -> Pair(filter, query) }
        .flatMapLatest { (filter, query) ->
            val source = when {
                query.isNotBlank() -> taskRepository.searchTasks(query)
                filter == TaskFilter.PENDING -> taskRepository.getPendingTasks()
                filter == TaskFilter.COMPLETED -> taskRepository.getCompletedTasks()
                else -> taskRepository.getAllTasks()
            }
            source.map { tasks ->
                TaskListUiState(
                    tasks = tasks,
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

    fun scheduleReminder(context: Context, taskId: Long, title: String, description: String?, remindAt: Long) {
        val data = workDataOf(
            ReminderWorker.KEY_TASK_ID to taskId,
            ReminderWorker.KEY_TASK_TITLE to title,
            ReminderWorker.KEY_TASK_DESC to description
        )

        val delay = remindAt - System.currentTimeMillis()
        if (delay <= 0) return

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("reminder_$taskId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_$taskId",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}