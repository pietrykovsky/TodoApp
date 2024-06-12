package com.todo.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.todo.app.db.NotificationDao

class NotificationViewModelFactory(private val notificationDao: NotificationDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(notificationDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
