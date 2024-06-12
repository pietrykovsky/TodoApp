package com.todo.app.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.todo.app.db.Notification
import com.todo.app.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationManagementPage(navController: NavHostController, viewModel: NotificationViewModel) {
    val notificationList by viewModel.notifications.observeAsState(initial = emptyList())

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = { viewModel.deleteAllNotifications() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete All")
                }
            }
            if (notificationList.isNotEmpty()) {
                LazyColumn {
                    itemsIndexed(notificationList) { _, item ->
                        NotificationItem(notification = item, onDelete = {
                            viewModel.deleteNotification(item)
                        })
                    }
                }
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "No notifications yet",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("notificationCreation") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .clip(CircleShape),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.inversePrimary
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Notification")
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onDelete: () -> Unit) {
    val formattedDate = SimpleDateFormat("HH:mm aa, dd/MM/yyyy", Locale.ENGLISH).format(Date(notification.reminderTime))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
            .clickable { /* Handle click if needed */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Reminder at: $formattedDate",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.inversePrimary
            )
            Text(
                text = "Task ID: ${notification.taskId}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}
