package com.todo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.todo.app.db.AppDatabase
import com.todo.app.ui.theme.TodoAppTheme
import com.todo.app.viewmodels.TodoViewModel
import com.todo.app.viewmodels.TodoViewModelFactory

class MainActivity : ComponentActivity() {
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(
            AppDatabase.getDatabase(this).taskDao(),
            AppDatabase.getDatabase(this).notificationDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(todoViewModel)
                }
            }
        }
    }
}