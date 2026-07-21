package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time

@Entity
data class Note(
	@PrimaryKey(autoGenerate = true)
	val id: Int? = null,
	val title: String,
	val description: String,
	val timeCreated: Time = Time(System.currentTimeMillis())
)
