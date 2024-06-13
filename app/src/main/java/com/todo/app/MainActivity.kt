package com.todo.app

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Observer
import com.todo.app.db.AppDatabase
import com.todo.app.ui.theme.TodoAppTheme
import com.todo.app.viewmodels.TodoViewModel
import com.todo.app.viewmodels.TodoViewModelFactory
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class MainActivity : ComponentActivity() {
    private val todoViewModel: TodoViewModel by viewModels {
        TodoViewModelFactory(
            AppDatabase.getDatabase(this).taskDao(),
            AppDatabase.getDatabase(this).notificationDao(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
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

        todoViewModel.showExactAlarmPermissionDialog.observe(this, Observer { show ->
            if (show) {
                showExactAlarmPermissionDialog()
            }
        })
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("default", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showExactAlarmPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Enable Exact Alarms")
            .setMessage("This app needs permission to schedule exact alarms. Please enable it in the app settings.")
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:" + packageName)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

