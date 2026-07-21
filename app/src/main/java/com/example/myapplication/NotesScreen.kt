package com.example.myapplication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
	state: NotesState,
	onEvent: (NotesEvents) -> Unit
) {
	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						text = "My Notes",
						fontWeight = FontWeight.Bold
					)
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer,
					titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
				)
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = {
					onEvent(NotesEvents.ShowDialog)
				},
				containerColor = MaterialTheme.colorScheme.primaryContainer,
				contentColor = MaterialTheme.colorScheme.onPrimaryContainer
			) {
				Icon(
					imageVector = Icons.Default.Add,
					contentDescription = "Add Note"
				)
			}
		}
	) { padding ->
		if (state.isAddingNote) {
			AddNoteDialog(state = state, onEvent = onEvent)
		}
		LazyColumn(
			contentPadding = padding,
			modifier = Modifier
				.fillMaxSize()
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
		) {
			item {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 16.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					NotesSortType.entries.forEach { sortType ->
						Row(
							modifier = Modifier
								.weight(1f)
								.clickable {
									onEvent(NotesEvents.SortNotes(sortType))
								},
							verticalAlignment = Alignment.CenterVertically,
							horizontalArrangement = Arrangement.Center
						) {
							RadioButton(
								selected = state.notesSortType == sortType,
								onClick = {
									onEvent(NotesEvents.SortNotes(sortType))
								}
							)
							Text(
								text = sortType.name,
								style = MaterialTheme.typography.labelLarge
							)
						}
					}
				}
			}
			items(state.notes) { note ->
				NoteItem(
					note = note,
					onEvent = onEvent,
					modifier = Modifier.fillMaxWidth()
				)
			}
			item {
				Spacer(modifier = Modifier.height(80.dp))
			}
		}
	}
}

@Composable
fun NoteItem(
	note: Note,
	onEvent: (NotesEvents) -> Unit,
	modifier: Modifier = Modifier
) {
	Card(
		modifier = modifier,
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surfaceVariant
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
	) {
		Row(
			modifier = Modifier
				.padding(16.dp)
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = note.title,
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.SemiBold
				)
				Spacer(modifier = Modifier.height(4.dp))
				Text(
					text = note.description,
					style = MaterialTheme.typography.bodyMedium
				)
				Spacer(modifier = Modifier.height(8.dp))
				Text(
					text = note.timeCreated.toString(),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
				)
			}
			IconButton(onClick = {
				onEvent(NotesEvents.DeleteNote(note))
			}) {
				Icon(
					imageVector = Icons.Default.Delete,
					contentDescription = "Delete Note",
					tint = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}

@Composable
fun AddNoteDialog(
	state: NotesState,
	onEvent: (NotesEvents) -> Unit,
	modifier: Modifier = Modifier
) {
	AlertDialog(
		modifier = modifier,
		onDismissRequest = {
			onEvent(NotesEvents.HideDialog)
		},
		confirmButton = {
			TextButton(onClick = { onEvent(NotesEvents.SaveNote) }) {
				Text("Save")
			}
		},
		dismissButton = {
			TextButton(onClick = {
				onEvent(NotesEvents.HideDialog)
			}) {
				Text("Cancel")
			}
		},
		title = {
			Text(text = "New Note")
		},
		text = {
			Column(
				modifier = Modifier.fillMaxWidth(),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				TextField(
					value = state.title,
					onValueChange = {
						onEvent(NotesEvents.SetTitle(it))
					},
					placeholder = {
						Text(text = "Title")
					},
					modifier = Modifier.fillMaxWidth(),
					singleLine = true
				)
				TextField(
					value = state.description,
					onValueChange = {
						onEvent(NotesEvents.SetDescription(it))
					},
					placeholder = {
						Text(text = "Description")
					},
					modifier = Modifier.fillMaxWidth(),
					minLines = 5
				)
			}
		}
	)
}
