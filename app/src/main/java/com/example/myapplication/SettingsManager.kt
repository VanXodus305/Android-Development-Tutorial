package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class SettingsManager(val context: Context) {
	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SETTINGS_DATASTORE")

	companion object {
		val SQUARE_COLOR = stringPreferencesKey("COLOR")
		val SQUARE_SIZE = intPreferencesKey("SIZE")
		val SQUARE_THICKNESS = intPreferencesKey("THICKNESS")
	}

	suspend fun saveSettings(settings: Settings) {
		context.dataStore.edit {
			it[SQUARE_COLOR] = settings.color
			it[SQUARE_SIZE] = settings.size
			it[SQUARE_THICKNESS] = settings.thickness
		}
	}

	fun getSettings() = context.dataStore.data.map {
		Settings(
			color = it[SQUARE_COLOR] ?: "FFFFFF",
			size = it[SQUARE_SIZE] ?: 100,
			thickness = it[SQUARE_THICKNESS] ?: 1,
		)
	}

}