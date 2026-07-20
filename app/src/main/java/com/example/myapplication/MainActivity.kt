package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {

	lateinit var settingsDataManager: SettingsManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		settingsDataManager = SettingsManager(this)

		enableEdgeToEdge()
		setContent {
			val settings by settingsDataManager.getSettings().collectAsState(initial = null)

			MaterialTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
//					Greeting("Android")
//					MyAppNavigation()
//					ToDoAppScreen()
					SettingsInput(settingsDataManager, settings)
				}
			}
		}
	}
}


@Preview(showBackground = true)
@Composable
fun Preview() {
	MaterialTheme {
//		Greeting("Android")
//		MyAppNavigation()
		ToDoAppScreen()
	}
}

