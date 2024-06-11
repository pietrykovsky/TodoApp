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

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _selectedPriority = MutableLiveData<Int?>(null)
    val selectedPriority: LiveData<Int?> get() = _selectedPriority

    private val _selectedSortOption = MutableLiveData("Date Ascending")
    val selectedSortOption: LiveData<String> get() = _selectedSortOption

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
}
