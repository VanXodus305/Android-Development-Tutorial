package com.example.myapplication

data class NotesState(
	val notes: List<Note> = emptyList(),
	val title: String = "",
	val description: String = "",
	val isAddingNote: Boolean = false,
	val notesSortType: NotesSortType = NotesSortType.TIME
)
