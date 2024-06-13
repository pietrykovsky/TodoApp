package com.todo.app.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.todo.app.db.Notification
import com.todo.app.db.Task
import com.todo.app.viewmodels.TodoViewModel
import java.util.*

@Composable
fun NotificationCreationPage(
    navController: NavHostController,
    todoViewModel: TodoViewModel
) {
    val taskList by todoViewModel.tasks.observeAsState(initial = emptyList())
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var reminderTime by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    var showTaskDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Create Notification",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            OutlinedButton(onClick = { showTaskDropdown = !showTaskDropdown }) {
                Text(text = selectedTask?.name ?: "Select Task")
            }
            DropdownMenu(
                expanded = showTaskDropdown,
                onDismissRequest = { showTaskDropdown = false }
            ) {
                taskList.forEach { task ->
                    DropdownMenuItem(
                        text = { Text(text = task.name) },
                        onClick = {
                            selectedTask = task
                            showTaskDropdown = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Reminder Time", fontSize = 16.sp)
        DatePicker(context = context, dateMillis = reminderTime) { date ->
            reminderTime = date.timeInMillis
        }
        Spacer(modifier = Modifier.height(8.dp))
        TimePicker(context = context, timeMillis = reminderTime) { time ->
            reminderTime = time.timeInMillis
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedTask?.let {
                    val notification = Notification(
                        taskId = it.id,
                        reminderTime = reminderTime
                    )
                    todoViewModel.addNotification(notification)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Notification", color = MaterialTheme.colorScheme.inversePrimary)
        }
    }
}

@Composable
fun DatePicker(context: Context, dateMillis: Long, onDateSelected: (Calendar) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = dateMillis }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            onDateSelected(calendar)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedButton(onClick = { datePickerDialog.show() }) {
        Text(text = "Select Date")
    }
}

@Composable
fun TimePicker(context: Context, timeMillis: Long, onTimeSelected: (Calendar) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = timeMillis }
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            onTimeSelected(calendar)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    OutlinedButton(onClick = { timePickerDialog.show() }) {
        Text(text = "Select Time")
    }
}
