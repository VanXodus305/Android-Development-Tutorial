package com.example.myapplication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(viewModel: DownloadViewModel) {
	val currentScreenState by viewModel.uiState.collectAsStateWithLifecycle()
	var inputUrl by remember { mutableStateOf("https://example.com") }
	var inputName by remember { mutableStateOf("Documentation.zip") }

	Scaffold(
		topBar = {
			TopAppBar(title = {
				Text(
					"Asynchronous Downloader Hub",
					fontWeight = FontWeight.Bold
				)
			})
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
				.padding(16.dp)
		) {
			// Form Input Column
			OutlinedTextField(
				value = inputUrl,
				onValueChange = { inputUrl = it },
				label = { Text("Target Resource URL Link") },
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(8.dp))
			OutlinedTextField(
				value = inputName,
				onValueChange = { inputName = it },
				label = { Text("Save Filename Destination") },
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(8.dp))

			Button(
				onClick = {
					if (inputUrl.isNotBlank() && inputName.isNotBlank()) {
						viewModel.startDownload(inputUrl, inputName)
					}
				},
				modifier = Modifier.align(Alignment.End)
			) {
				Text("Enqueue Work Task")
			}

			Spacer(modifier = Modifier.height(16.dp))
			Text("Active Thread Tasks Queue", style = MaterialTheme.typography.titleMedium)
			Spacer(modifier = Modifier.height(8.dp))

			// Pattern Matching over the StateFlow content via declarative composition
			when (val state = currentScreenState) {
				is DownloadUiState.Empty -> {
					Box(
						modifier = Modifier
							.weight(1f)
							.fillMaxWidth(),
						contentAlignment = Alignment.Center
					) {
						Text(
							"No background thread workers enqueued.",
							color = MaterialTheme.colorScheme.outline
						)
					}
				}

				is DownloadUiState.Active -> {
					LazyColumn(
						verticalArrangement = Arrangement.spacedBy(8.dp),
						modifier = Modifier.weight(1f)
					) {
						items(state.tasks, key = { it.id }) { task ->
							DownloadRowCard(task)
						}
					}
				}
			}
		}
	}
}

@Composable
fun DownloadRowCard(task: DownloadTask) {
	Card(modifier = Modifier.fillMaxWidth()) {
		Column(modifier = Modifier.padding(12.dp)) {
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
				Text(task.fileName, fontWeight = FontWeight.SemiBold)
				Text(task.status.name, color = MaterialTheme.colorScheme.secondary)
			}
			Spacer(modifier = Modifier.height(8.dp))
			LinearProgressIndicator(
				progress = { task.progress / 100f },
				modifier = Modifier.fillMaxWidth()
			)
			Spacer(modifier = Modifier.height(4.dp))
			Text(
				"Transfer Completion Metrics: ${task.progress}%",
				style = MaterialTheme.typography.labelSmall
			)
		}
	}
}