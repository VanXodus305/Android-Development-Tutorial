package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsInput(
	settingsDataManager: SettingsManager,
	settings: Settings?,
) {
	val scope = rememberCoroutineScope()
	var colorState by remember { mutableStateOf(TextFieldValue()) }
	var sizeState by remember { mutableStateOf(TextFieldValue()) }
	var thicknessState by remember { mutableStateOf(TextFieldValue()) }

	// Synchronize text fields with saved settings when they load
	LaunchedEffect(settings) {
		settings?.let {
			colorState = TextFieldValue(it.color)
			sizeState = TextFieldValue(it.size.toString())
			thicknessState = TextFieldValue(it.thickness.toString())
		}
	}

	Column(
		modifier = Modifier
			.padding(8.dp)
			.padding(top = 32.dp)
	) {
		TextField(
			value = colorState,
			onValueChange = { colorState = it },
			label = { Text(text = "Square color (Hex)") },
			modifier = Modifier
				.padding(vertical = 4.dp)
				.fillMaxWidth(),
			placeholder = { Text("FFFFFF") }
		)
		TextField(
			value = sizeState,
			onValueChange = { sizeState = it },
			label = { Text(text = "Square size") },
			modifier = Modifier
				.padding(vertical = 4.dp)
				.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
		)
		TextField(
			value = thicknessState,
			onValueChange = { thicknessState = it },
			label = { Text(text = "Square thickness") },
			modifier = Modifier
				.padding(vertical = 4.dp)
				.fillMaxWidth(),
			keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
		)
		Button(onClick = {
			scope.launch(Dispatchers.IO) {
				val color = colorState.text.ifBlank { "FFFFFF" }
				val size = sizeState.text.toIntOrNull() ?: 100
				val thickness = thicknessState.text.toIntOrNull() ?: 1

				settingsDataManager.saveSettings(
					Settings(
						color = color,
						size = size,
						thickness = thickness
					)
				)
			}
		}, modifier = Modifier.fillMaxWidth()) {
			Text(text = "Save")
		}

		settings?.let { saved ->
			val parsedColor = try {
				// Ensure hex string starts with # for parseColor
				val hex = if (saved.color.startsWith("#")) saved.color else "#${saved.color}"
				Color(hex.toColorInt())
			} catch (e: Exception) {
				Log.e("SettingsInput", "Error parsing color: ${saved.color}", e)
				Color.Gray
			}

			Card(
				modifier = Modifier
					.padding(top = 16.dp)
					.width(saved.size.dp)
					.height(saved.size.dp),
				elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
				border = BorderStroke(
					width = saved.thickness.dp,
					color = Color.Black
				),
				colors = CardDefaults.cardColors(
					containerColor = parsedColor
				)
			) {
				Column(modifier = Modifier.padding(8.dp)) {
					Text(text = "Color: #${saved.color}")
					Text(text = "Size: ${saved.size}")
					Text(text = "Thickness: ${saved.thickness}")
				}
			}
		}
	}
}
