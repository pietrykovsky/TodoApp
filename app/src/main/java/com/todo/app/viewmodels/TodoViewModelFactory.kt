package com.todo.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todo.app.db.NotificationDao
import com.todo.app.db.TaskDao

class TodoViewModelFactory(
    private val taskDao: TaskDao,
    private val notificationDao: NotificationDao,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(taskDao, notificationDao, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
