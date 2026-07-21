package com.example.myapplication

sealed interface NotesEvents {
	object SaveNote : NotesEvents
	data class SetTitle(val title: String) : NotesEvents
	data class SetDescription(val description: String) : NotesEvents
	object ShowDialog : NotesEvents
	object HideDialog : NotesEvents
	data class SortNotes(val notesSortType: NotesSortType) : NotesEvents
	data class DeleteNote(val note: Note) : NotesEvents
}