package com.todo.app.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.app.AlarmReceiver
import com.todo.app.db.Notification
import com.todo.app.db.NotificationDao
import com.todo.app.db.Task
import com.todo.app.db.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoViewModel(
    private val taskDao: TaskDao,
    private val notificationDao: NotificationDao,
    private val context: Context
) : ViewModel() {
    private val _showExactAlarmPermissionDialog = MutableLiveData<Boolean>()
    val showExactAlarmPermissionDialog: LiveData<Boolean> get() = _showExactAlarmPermissionDialog

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> get() = _notifications

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _selectedPriority = MutableLiveData<Int?>(null)
    val selectedPriority: LiveData<Int?> get() = _selectedPriority

    private val _selectedSortOption = MutableLiveData("Date Ascending")
    val selectedSortOption: LiveData<String> get() = _selectedSortOption

    init {
        fetchTasks()
        fetchNotifications()
    }

    private fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.postValue(taskDao.getAll())
        }
    }

    private fun fetchNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            removeStaleNotifications()
            _notifications.postValue(notificationDao.getAll())
        }
    }

    private fun removeStaleNotifications() {
        val currentTime = System.currentTimeMillis()
        val staleNotifications = notificationDao.getAll().filter {
            it.reminderTime < currentTime && it.repeatInterval == null
        }

        staleNotifications.forEach { notification ->
            notificationDao.delete(notification)
            cancelAlarm(notification)
        }
    }

    fun getTask(taskId: Int): LiveData<Task> {
        return taskDao.getTask(taskId)
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.add(task)
            fetchTasks()
        }
    }

    fun addNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.add(notification)
            scheduleAlarm(notification)
            fetchNotifications()
        }
    }

    fun editTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.edit(task)
            fetchTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.deleteAllWhere(task.id)
            fetchNotifications()
            taskDao.delete(task)
            fetchTasks()
        }
    }

    fun deleteNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.delete(notification)
            cancelAlarm(notification)
            fetchNotifications()
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationDao.deleteAll()
            cancelAllAlarms()
            fetchNotifications()
        }
    }

    fun filterAndSortTasks(
        taskList: List<Task>,
        query: String,
        priority: Int?,
        sortOption: String
    ): List<Task> {
        _searchQuery.value = query
        _selectedPriority.value = priority
        _selectedSortOption.value = sortOption

        var filteredTasks = taskList.filter { task ->
            task.name.contains(query, ignoreCase = true) || (task.description?.contains(query, ignoreCase = true) ?: false)
        }

        if (priority != null) {
            filteredTasks = filteredTasks.filter { it.priority == priority }
        }

        filteredTasks = when (sortOption) {
            "Date Ascending" -> filteredTasks.sortedBy { it.createdAt }
            "Date Descending" -> filteredTasks.sortedByDescending { it.createdAt }
            "Priority Ascending" -> filteredTasks.sortedBy { it.priority }
            "Priority Descending" -> filteredTasks.sortedByDescending { it.priority }
            "Alphabetically Ascending" -> filteredTasks.sortedBy { it.name }
            "Alphabetically Descending" -> filteredTasks.sortedByDescending { it.name }
            else -> filteredTasks
        }

        return filteredTasks
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedPriority.value = null
        _selectedSortOption.value = "Date Ascending"
    }

    private fun scheduleAlarm(notification: Notification) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val taskName = runBlocking(Dispatchers.IO) {
            taskDao.getTaskSync(notification.taskId)?.name ?: return@runBlocking null
        } ?: return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("TASK_NAME", taskName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (notification.repeatInterval != null) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    notification.reminderTime,
                    notification.repeatInterval,
                    pendingIntent
                )
            } else {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        notification.reminderTime,
                        pendingIntent
                    )
                } else {
                    showExactAlarmPermissionDialog()
                }
            }
        } catch (e: SecurityException) {
            showExactAlarmPermissionDialog()
        }
    }

    private fun showExactAlarmPermissionDialog() {
        _showExactAlarmPermissionDialog.postValue(true)
    }

    private fun cancelAlarm(notification: Notification) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun cancelAllAlarms() {
        notifications.value?.forEach { notification ->
            cancelAlarm(notification)
        }
    }
}
