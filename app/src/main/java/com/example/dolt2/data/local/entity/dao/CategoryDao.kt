package com.example.dolt2.data.local.entity.dao

import androidx.room.*
import com.example.dolt2.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {

    @Query("SELECT * FROM category ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fund getCategoryById(id: Long): CategoryEntity?
}