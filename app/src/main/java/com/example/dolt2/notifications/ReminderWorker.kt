package com.example.dolt2.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dolt2.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TASK_TITLE = "task_title"
        const val KEY_TASK_DESC = "task_description"
    }

    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1L)
        val title = inputData.getString(KEY_TASK_TITLE) ?: return Result.failure()
        val description = inputData.getString(KEY_TASK_DESC)

        //compruebo q la tarea sigue existiendo y no esta completada
        val task = taskRepository.getTaskById(taskId)
        if (task == null || task.isCompleted) return Result.success()

        NotificationHelper.showNotification(
            context = applicationContext,
            notificationId = taskId.toInt(),
            title = title,
            description = description
        )

        return Result.success()
    }
}