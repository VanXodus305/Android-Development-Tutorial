package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(
	private val dao: NotesDAO
) : ViewModel() {
	private val notesSortType = MutableStateFlow(NotesSortType.TIME)
	private val _notes = notesSortType.flatMapLatest { sortType ->
		when (sortType) {
			NotesSortType.TIME -> dao.getAllNotesbyTime()
			NotesSortType.TITLE -> dao.getAllNotesbyTitle()
		}
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
	private val _state = MutableStateFlow(NotesState())
	val state = combine(_state, notesSortType, _notes) { state, sortType, notes ->
		state.copy(
			notes = notes,
			notesSortType = sortType
		)
	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesState())


	fun onEvent(event: NotesEvents) {
		when (event) {
			is NotesEvents.DeleteNote -> {
				viewModelScope.launch {
					dao.deleteNote(event.note)
				}
			}

			NotesEvents.HideDialog -> {
				_state.update {
					it.copy(
						isAddingNote = false
					)
				}
			}

			NotesEvents.SaveNote -> {
				val title = state.value.title
				val description = state.value.description

				if (title.isBlank() || description.isBlank()) {
					return
				}
				val note = Note(
					title = title,
					description = description
				)
				viewModelScope.launch {
					dao.insertNote(note)
				}
				_state.update {
					it.copy(
						isAddingNote = false,
						title = "",
						description = ""
					)
				}
			}

			is NotesEvents.SetDescription -> {
				_state.update {
					it.copy(
						description = event.description
					)
				}
			}

			is NotesEvents.SetTitle -> {
				_state.update {
					it.copy(
						title = event.title
					)
				}
			}

			NotesEvents.ShowDialog -> {
				_state.update {
					it.copy(
						isAddingNote = true
					)
				}
			}

			is NotesEvents.SortNotes -> {
				notesSortType.value = event.notesSortType

			}
		}
	}
}