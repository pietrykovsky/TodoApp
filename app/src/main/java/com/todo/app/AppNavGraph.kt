package com.todo.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(viewModel: TodoViewModel, startDestination: String = "taskList") {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("taskList") {
            TodoListPage(navController, viewModel)
        }
        composable("taskCreation") {
            TaskCreationPage(navController, viewModel)
        }
        composable("taskEdit/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toInt() ?: return@composable
            EditTaskPage(navController, taskId, viewModel)
        }
    }
}
