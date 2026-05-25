package com.example.dolt2.di

import android.content.Context
import androidx.room.Room
import com.example.dolt2.data.local.DoltDatabase
import com.example.dolt2.data.local.dao.CategoryDao
import com.example.dolt2.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDoltDatabase(@ApplicationContext context: Context): DoltDatabase =
        Room.databaseBuilder(
            context,
            DoltDatabase::class.java,
            "dolt.db"
        ).build()

    @Provides
    fun provideTaskDao(db: DoltDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideCategoryDao(db: DoltDatabase): CategoryDao = db.categoryDao()
}