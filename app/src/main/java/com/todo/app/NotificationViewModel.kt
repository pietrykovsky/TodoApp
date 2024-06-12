package com.todo.app

import androidx.lifecycle.*
import com.todo.app.db.Notification
import com.todo.app.db.NotificationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationViewModel(private val notificationDao: NotificationDao) : ViewModel() {

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            _notifications.postValue(notificationDao.getAll())
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.add(notification)
            fetchNotifications()
        }
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.delete(notification)
            fetchNotifications()
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.deleteAll()
            fetchNotifications()
        }
    }
}