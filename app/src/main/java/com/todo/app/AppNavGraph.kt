package com.todo.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.todo.app.pages.NotificationCreationPage
import com.todo.app.pages.NotificationManagementPage

@Composable
fun AppNavGraph(
    todoViewModel: TodoViewModel,
    notificationViewModel: NotificationViewModel,
    startDestination: String = "taskList"
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("taskList") {
            TodoListPage(navController, todoViewModel)
        }
        composable("taskCreation") {
            TaskCreationPage(navController, todoViewModel)
        }
        composable("taskEdit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: return@composable
            EditTaskPage(navController, taskId, todoViewModel)
        }
        composable("notifications") {
            NotificationManagementPage(navController, notificationViewModel)
        }
        composable("notificationCreation") {
            NotificationCreationPage(navController, todoViewModel, notificationViewModel)
        }
    }
}
