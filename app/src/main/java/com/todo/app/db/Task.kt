package com.todo.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String?,
    val priority: Int
)
