package com.example.todos.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg task: Task)

    @Delete
    suspend fun delete(vararg task: Task)

    @Query("SELECT * FROM task")
    suspend fun getAll(): List<Task>

    @Update
    suspend fun update(vararg task: Task)

    @Query("SELECT * FROM task")
    fun getAllAsFlow(): Flow<List<Task>>
}