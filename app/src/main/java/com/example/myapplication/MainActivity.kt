package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
	
//	lateinit var settingsDataManager: SettingsManager

	val viewModel: DownloadViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
//		settingsDataManager = SettingsManager(this)

		enableEdgeToEdge()
		setContent {

			MaterialTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
//					Greeting("Android")
//					MyAppNavigation()
//					ToDoAppScreen()
//					val settings by settingsDataManager.getSettings().collectAsState(initial = null)
//					SettingsInput(settingsDataManager, settings)
					DownloadScreen(viewModel = viewModel)
				}
			}
		}
	}

//	@Preview(showBackground = true)
//	@Composable
//	fun Preview() {
//		MaterialTheme {
////		Greeting("Android")
////		MyAppNavigation()
////		ToDoAppScreen()
////		DownloadScreen(viewModel = viewModel)
//		}
//	}
}

