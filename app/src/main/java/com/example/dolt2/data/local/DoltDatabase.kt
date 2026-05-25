package com.example.dolt2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dolt2.data.local.dao.CategoryDao
import com.example.dolt2.data.local.dao.TaskDao
import com.example.dolt2.data.local.entity.CategoryEntity
import com.example.dolt2.data.local.entity.ReminderEntity
import com.example.dolt2.data.local.entity.TaskEntity


@Database(
    entities = [TaskEntity::class, CategoryEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DoltDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}