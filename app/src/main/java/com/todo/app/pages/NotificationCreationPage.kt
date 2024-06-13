package com.todo.app.pages

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.navigation.NavHostController
import com.todo.app.db.Notification
import com.todo.app.db.Task
import com.todo.app.viewmodels.TodoViewModel
import java.text.SimpleDateFormat
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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Select Task", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedButton(
                        onClick = { showTaskDropdown = !showTaskDropdown },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedTask?.name ?: "Select Task",
                            color = MaterialTheme.colorScheme.inversePrimary
                        )
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

                Text(text = "Reminder Time", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                DatePicker(context = context, dateMillis = reminderTime) { date ->
                    reminderTime = date.timeInMillis
                }
                Spacer(modifier = Modifier.height(8.dp))
                TimePicker(context = context, timeMillis = reminderTime) { time ->
                    reminderTime = time.timeInMillis
                }
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.inversePrimary
                )
            ) {
                Text("Save Notification", color = MaterialTheme.colorScheme.inversePrimary)
            }
        }
    }
}

@Composable
fun DatePicker(context: Context, dateMillis: Long, onDateSelected: (Calendar) -> Unit) {
    val selectedDate by remember { mutableStateOf(Calendar.getInstance().apply { timeInMillis = dateMillis }) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate.set(year, month, dayOfMonth)
            onDateSelected(selectedDate)
        },
        selectedDate.get(Calendar.YEAR),
        selectedDate.get(Calendar.MONTH),
        selectedDate.get(Calendar.DAY_OF_MONTH)
    )

    OutlinedButton(onClick = { datePickerDialog.show() }) {
        Text(
            text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(selectedDate.timeInMillis)),
            color = MaterialTheme.colorScheme.inversePrimary
        )
    }
}

@Composable
fun TimePicker(context: Context, timeMillis: Long, onTimeSelected: (Calendar) -> Unit) {
    val selectedTime by remember { mutableStateOf(Calendar.getInstance().apply { timeInMillis = timeMillis }) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            onTimeSelected(selectedTime)
        },
        selectedTime.get(Calendar.HOUR_OF_DAY),
        selectedTime.get(Calendar.MINUTE),
        true
    )

    OutlinedButton(onClick = { timePickerDialog.show() }) {
        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(selectedTime.timeInMillis)),
            color = MaterialTheme.colorScheme.inversePrimary
        )
    }
}

