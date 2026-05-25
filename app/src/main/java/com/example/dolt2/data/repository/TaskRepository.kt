package com.example.dolt2.data.repository


import com.example.dolt2.data.local.dao.TaskDao
import com.example.dolt2.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<TaskEntity>> =
        taskDao.getAllTasks()

    fun getPendingTasks(): Flow<List<TaskEntity>> =
        taskDao.getPendingTasks()

    fun getCompletedTasks(): Flow<List<TaskEntity>> =
        taskDao.getCompletedTasks()

    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByCategory(categoryId)

    fun searchTasks(query: String): Flow<List<TaskEntity>> =
        taskDao.searchTasks(query)

    suspend fun getTaskById(id: Long): TaskEntity? =
        taskDao.getTaskById(id)

    suspend fun insertTask(task: TaskEntity): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) =
        taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) =
        taskDao.deleteTask(task)

    suspend fun toggleCompleted(id: Long, currentState: Boolean) =
        taskDao.setCompleted(id, !currentState)

    suspend fun deleteAllCompleted() =
        taskDao.deleteAllCompleted()
}