package com.todo.app.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    fun add(task: Task)

    @Update
    fun edit(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTask(taskId: Int): LiveData<Task>
}
