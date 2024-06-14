package com.todo.app.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(notification: Notification)

    @Update
    fun edit(notification: Notification)

    @Delete
    fun delete(notification: Notification)

    @Query("DELETE FROM notifications WHERE taskId = :taskId")
    fun deleteAllWhere(taskId: Int)

    @Query("DELETE FROM notifications")
    fun deleteAll()

    @Query("SELECT * FROM notifications ORDER BY reminderTime ASC")
    fun getAll(): List<Notification>
}
