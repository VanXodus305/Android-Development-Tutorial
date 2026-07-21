package com.example.myapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDAO {
	@Upsert
	suspend fun insertNote(note: Note)

	@Delete
	suspend fun deleteNote(note: Note)

	@Query("SELECT * FROM Note ORDER BY timeCreated DESC")
	fun getAllNotesbyTime(): Flow<List<Note>>

	@Query("SELECT * FROM Note ORDER BY title ASC")
	fun getAllNotesbyTitle(): Flow<List<Note>>
}