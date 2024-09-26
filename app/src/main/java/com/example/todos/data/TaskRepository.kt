package com.example.todos.data

import com.example.todos.data.db.Task
import com.example.todos.data.db.TaskDao
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

    fun getTasksAsFlow(): Flow<List<Task>> {
        return taskDao.getAllAsFlow()
    }

    suspend fun addTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task) = coroutineScope {
        taskDao.delete(task)
    }

}
