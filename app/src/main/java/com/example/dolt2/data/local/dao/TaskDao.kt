package com.example.dolt2.data.local.dao

import androidx.room.*
import com.example.dolt2.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("""
        SELECT * FROM task
        ORDER BY
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END,
            dueDate ASC,
            createdAt DESC
    """)
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM task WHERE isCompleted = 0
        ORDER BY
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END,
            dueDate ASC
    """)
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM task WHERE categoryId = :categoryId
        ORDER BY
            CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END,
            dueDate ASC
    """)
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM task
        WHERE title LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE task SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM task WHERE isCompleted = 1")
    suspend fun deleteAllCompleted()
}