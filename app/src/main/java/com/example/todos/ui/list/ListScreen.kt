package com.example.todos.ui.list

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.todos.data.db.Task
import com.example.todos.ui.theme.TodosTheme

@Composable
@Preview
fun PreviewListScreen() {
    val tasks = listOf(Task(1, "Item 1", true), Task(2, "Item 2", false), Task(3, "Item 3", true))
    val state = TaskUiState(tasks = tasks, isError = false)
    TodosTheme {
        TasksListScreen(
            modifier = Modifier,
            state = state,
            {},
            { _: Task -> },
            { _: Task -> })
    }
}

@Composable
fun TasksListRoute(modifier: Modifier = Modifier, viewModel: TasksViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle(TaskUiState())

    TasksListScreen(
        modifier,
        state,
        viewModel::addTask,
        viewModel::updateTask,
        viewModel::deleteTask
    )
}


@Composable
fun TasksListScreen(
    modifier: Modifier,
    state: TaskUiState,
    saveTask: (Task) -> Unit,
    updateTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit
) {
    LogCompositions(tag = "TasksListScreen")

    when {
        state.isError -> ErrorScreen(modifier, state)
        else -> ListScreenContent(modifier, state, saveTask, updateTask, deleteTask)
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, state: TaskUiState) {
    Text("Error: List has size of ${state.tasks.size}")
}

@Composable
private fun ListScreenContent(
    modifier: Modifier = Modifier,
    state: TaskUiState,
    saveTask: (Task) -> Unit,
    updateTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        CreateTaskItem({ showDialog = false }, saveTask)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { showDialog = true },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "icon")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            items(state.tasks) {
                Spacer(modifier = Modifier.padding(4.dp))
                TaskItem(
                    modifier = modifier,
                    task = it,
                    updateTask,
                    deleteTask,
                )
                Spacer(modifier = Modifier.padding(4.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Black)
            }
        }
    }
}

@Composable
private fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    updateTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit
) {

    fun toggle(isChecked: Boolean) {
        val newTodo = task.copy(completed = isChecked)
        updateTask(newTodo)
    }

    fun delete() {
        deleteTask(task)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = task.completed, onCheckedChange = ::toggle)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = task.name)
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = "delete",
            modifier = Modifier.clickable { delete() })
        Spacer(modifier = Modifier.padding(4.dp))
    }
}

@Preview
@Composable
fun PreviewNewTaskItem() {
    TodosTheme {
        CreateTaskItem({}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskItem(onDismiss: () -> Unit, onAddNewTodo: (Task) -> Unit) {
    var text by remember { mutableStateOf("") }
    var isDone by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter text") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { isDone = it }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(text = "Complete?")
                }
                Spacer(
                    modifier =
                    Modifier.height(24.dp)
                )
                TextButton(onClick = {
                    onAddNewTodo(Task(0, text, isDone))
                    onDismiss()
                }, modifier = Modifier.align(Alignment.End)) { Text("Save") }
            }
        }
    }
}

class Ref(var value: Int)

/**
 * Note the inline function below which ensures that this function is essentially
 * copied at the call site to ensure that its logging only recompositions from the
 * original call site.
 * Author: @vinaygaba
 */
@Composable
@Suppress("NOTHING_TO_INLINE")
inline fun LogCompositions(
    tag: String,
    msg: String = "",
    shouldLog: (count: Int) -> Boolean = { true }
) {
    val ref = remember { Ref(0) }
    SideEffect { ref.value++ }
    if (shouldLog(ref.value)) {
        Log.d(tag, "Compositions: $msg ${ref.value}")
    }
}
