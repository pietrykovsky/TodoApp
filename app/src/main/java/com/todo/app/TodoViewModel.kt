package com.todo.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.app.db.Task
import com.todo.app.db.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            _tasks.postValue(taskDao.getAll())
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

    fun editTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.edit(task)
            fetchTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.delete(task)
            fetchTasks()
        }
    }
}
