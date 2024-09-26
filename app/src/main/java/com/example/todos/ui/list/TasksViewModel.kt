package com.example.todos.ui.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todos.data.TaskRepository
import com.example.todos.data.db.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private val _uiState = MutableSharedFlow<TaskUiState>()
    val uiState: SharedFlow<TaskUiState> = _uiState.asSharedFlow()

    init {
        subscribeTasks()
    }

    private fun subscribeTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskRepository.getTasksAsFlow()
                    .collect { tasks ->
                    withContext(Dispatchers.Main) {
                        _uiState.emit(TaskUiState(tasks = tasks))
                    }
                }
            } catch (ex: Exception) {
                _uiState.emit(_uiState.last().copy(isError = true))
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}

@Immutable
data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isError: Boolean = false,
)
