package com.todo.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        // Volatile variable to ensure atomic access to the variable across threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the instance if it’s already initialized, otherwise create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_database"
                ).fallbackToDestructiveMigration() // Handle migrations appropriately
                    .build()
                INSTANCE = instance
                // Return the newly created database
                instance
            }
        }
    }
}