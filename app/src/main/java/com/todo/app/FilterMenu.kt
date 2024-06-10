package com.todo.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        Icon(imageVector = Icons.Default.Search, contentDescription = "Filters", tint = Color.White)
    }
}

@Composable
fun FilterMenu(
    onDismiss: () -> Unit,
    onApplyFilters: (String, Int?, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf<Int?>(null) }
    var selectedSortOption by remember { mutableStateOf("Date Ascending") }

    val sortOptions = listOf("Date Ascending", "Date Descending", "Priority Ascending", "Priority Descending", "Alphabetically Ascending", "Alphabetically Descending")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Tasks") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Priority", fontSize = 16.sp)
                PriorityDropdown(selectedPriority) { priority -> selectedPriority = priority }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Sort By", fontSize = 16.sp)
                SortDropdown(selectedSortOption, sortOptions) { option -> selectedSortOption = option }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilters(searchQuery, selectedPriority, selectedSortOption)
                    onDismiss()
                }
            ) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
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
            Text(text = selectedPriority?.toString() ?: "All")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(text = priority?.toString() ?: "All") },
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
            Text(text = selectedSortOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onSortOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
