package com.example.dolt2.data.local.entity // Corregido el package para que coincida con tu proyecto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Asegúrate de que TaskEntity existe y está importada si está en otro paquete
// import com.example.dolt2.data.local.entity.TaskEntity

@Entity(
    tableName = "reminder",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"], // Asegúrate de que en TaskEntity la PK se llame "id"
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["taskId"], unique = true)]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remindAt: Long,
    val isActive: Boolean = true,
    val taskId: Long
)