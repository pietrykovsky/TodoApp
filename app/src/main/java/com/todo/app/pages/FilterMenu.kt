package com.todo.app.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.todo.app.viewmodels.TodoViewModel

@Composable
fun FilterButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .size(48.dp) // Ensure the button is round by setting equal width and height
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Filters", tint = MaterialTheme.colorScheme.inversePrimary)
    }
}

@Composable
fun FilterMenu(
    onDismiss: () -> Unit,
    onApplyFilters: (String, Int?, String) -> Unit,
    viewModel: TodoViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.observeAsState("")
    val selectedPriority by viewModel.selectedPriority.observeAsState(null)
    val selectedSortOption by viewModel.selectedSortOption.observeAsState("Date Ascending")

    var currentSearchQuery by remember { mutableStateOf(searchQuery) }
    var currentSelectedPriority by remember { mutableStateOf(selectedPriority) }
    var currentSelectedSortOption by remember { mutableStateOf(selectedSortOption) }

    val sortOptions = listOf("Date Ascending", "Date Descending", "Priority Ascending", "Priority Descending", "Alphabetically Ascending", "Alphabetically Descending")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Tasks", color = MaterialTheme.colorScheme.inversePrimary) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = currentSearchQuery,
                    onValueChange = { currentSearchQuery = it },
                    label = { Text("Search", color = MaterialTheme.colorScheme.inversePrimary) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Priority", fontSize = 16.sp, color = MaterialTheme.colorScheme.inversePrimary)
                PriorityDropdown(currentSelectedPriority) { priority -> currentSelectedPriority = priority }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Sort By", fontSize = 16.sp, color = MaterialTheme.colorScheme.inversePrimary)
                SortDropdown(currentSelectedSortOption, sortOptions) { option -> currentSelectedSortOption = option }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(currentSearchQuery, currentSelectedPriority, currentSelectedSortOption)
                    onDismiss()
                }
            ) {
                Text("Apply Filters", color = MaterialTheme.colorScheme.inversePrimary)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.inversePrimary)
            }
        }
    )
}

@Composable
fun PriorityDropdown(selectedPriority: Int?, onPrioritySelected: (Int?) -> Unit) {
    val priorities = listOf(null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedPriority?.toString() ?: "All", color = MaterialTheme.colorScheme.inversePrimary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(text = priority?.toString() ?: "All", color = MaterialTheme.colorScheme.inversePrimary) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SortDropdown(selectedSortOption: String, sortOptions: List<String>, onSortOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = selectedSortOption, color = MaterialTheme.colorScheme.inversePrimary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, color = MaterialTheme.colorScheme.inversePrimary) },
                    onClick = {
                        onSortOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
