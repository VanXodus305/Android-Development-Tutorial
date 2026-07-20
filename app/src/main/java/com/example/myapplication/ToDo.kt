package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.UUID


data class ToDoItem(
	val id: String = UUID.randomUUID().toString(),
	val title: String,
	val priority: Priority,
	var isCompleted: Boolean = false
)


enum class Priority(val label: String, val color: Color) {
	HIGH("High", Color(0xFFE57373)),
	MEDIUM("Medium", Color(0xFFFFB74D)),
	LOW("Low", Color(0xFF81C784))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoAppScreen() {
	val todoList = remember {
		mutableStateListOf(
			ToDoItem(
				title = "Review Week 2 Activity Lifecycles",
				priority = Priority.HIGH,
				isCompleted = true
			),
			ToDoItem(title = "Fix VS Code Gradle compiler bugs", priority = Priority.HIGH),
			ToDoItem(title = "Design Student UI using Material3 Cards", priority = Priority.MEDIUM),
			ToDoItem(title = "Watch Type-Safe Navigation Crash Course", priority = Priority.LOW)
		)
	}

	var taskInput by rememberSaveable { mutableStateOf("") }
	var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
	var dropdownExpanded by remember { mutableStateOf(false) }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Task Tracker", fontWeight = FontWeight.Bold) },
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer,
					titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
				)
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize()
				.padding(16.dp)
		) {
			OutlinedTextField(
				value = taskInput,
				onValueChange = { taskInput = it },
				label = { Text("What needs to be done?") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)

			Spacer(modifier = Modifier.height(8.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Box {
					Button(onClick = { dropdownExpanded = true }) {
						Text("Priority: ${selectedPriority.label}")
					}
					DropdownMenu(
						expanded = dropdownExpanded,
						onDismissRequest = { dropdownExpanded = false }
					) {
						Priority.entries.forEach { priority ->
							DropdownMenuItem(
								text = { Text(priority.label) },
								onClick = {
									selectedPriority = priority
									dropdownExpanded = false
								}
							)
						}
					}
				}

				Button(
					onClick = {
						if (taskInput.isNotBlank()) {
							todoList.add(ToDoItem(title = taskInput.trim(), priority = selectedPriority))
							taskInput = ""
						}
					},
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
				) {
					Text("Add Task")
				}
			}

			Spacer(modifier = Modifier.height(16.dp))
			Text(
				"Your Tasks",
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.SemiBold
			)
			Spacer(modifier = Modifier.height(8.dp))

			LazyColumn(
				verticalArrangement = Arrangement.spacedBy(8.dp),
				modifier = Modifier.fillMaxSize()
			) {
				items(todoList, key = { it.id }) { item ->
					ToDoRowItem(
						item = item,
						onCheckedChange = { isChecked ->
							// Find element and map mutations accurately
							val index = todoList.indexOf(item)
							if (index != -1) {
								todoList[index] = item.copy(isCompleted = isChecked)
							}
						},
						onDeleteClick = {
							todoList.remove(item)
						}
					)
				}
			}
		}
	}
}