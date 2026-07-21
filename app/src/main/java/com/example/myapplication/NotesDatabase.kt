package com.example.myapplication

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.sql.Time

@Database(
	entities = [Note::class],
	version = 1
)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase() {
	abstract val dao: NotesDAO
}

class Converters {
	@TypeConverter
	fun fromTimestamp(value: Long?): Time? {
		return value?.let { Time(it) }
	}

	@TypeConverter
	fun dateToTimestamp(date: Time?): Long? {
		return date?.time
	}
}
