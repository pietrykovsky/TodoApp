package com.todo.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.todo.app.db.Task
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TodoListPage(navController: NavHostController, viewModel: TodoViewModel) {
    val taskList by viewModel.tasks.observeAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            if (taskList.isNotEmpty()) {
                LazyColumn {
                    itemsIndexed(taskList) { _, item ->
                        TodoItem(item = item, onDelete = {
                            viewModel.deleteTask(item)
                        }, onEdit = {
                            navController.navigate("taskEdit/${item.id}")
                        })
                    }
                }
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No items yet",
                    fontSize = 16.sp
                )
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("taskCreation") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
        }
    }
}

@Composable
fun TodoItem(item: Task, onDelete: () -> Unit, onEdit: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clickable { expanded = !expanded },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            val formattedDate = SimpleDateFormat("HH:mm aa, dd/MM/yyyy", Locale.ENGLISH).format(Date(item.createdAt))
            Text(
                text = "Created at: $formattedDate",
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Text(
                text = item.name,
                fontSize = 20.sp,
                color = Color.White
            )
            if (expanded) {
                Text(
                    text = item.description ?: "",
                    fontSize = 16.sp,
                    color = Color.White
                )
            } else {
                Text(
                    text = item.description?.take(20)?.plus("...") ?: "",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Text(
                text = "Priority: ${item.priority}",
                fontSize = 12.sp,
                color = Color.White
            )
        }
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}
