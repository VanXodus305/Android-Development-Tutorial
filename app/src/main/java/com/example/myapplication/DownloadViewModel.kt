package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class DownloadViewModel(application: Application) : AndroidViewModel(application) {

	private val workManager = WorkManager.getInstance(application)
	private val rawTasksList = mutableListOf<DownloadTask>()

	// Exposed hot StateFlow streaming state to the Compose views reactively
	private val _uiState = MutableStateFlow<DownloadUiState>(DownloadUiState.Empty)
	val uiState: StateFlow<DownloadUiState> = _uiState.asStateFlow()

	fun startDownload(url: String, name: String) {
		val taskId = UUID.randomUUID().toString()
		val newTask = DownloadTask(id = taskId, fileName = name, url = url)

		rawTasksList.add(newTask)
		updateState()

		// Preparing data keys payload package inputs for the background worker
		val inputData = Data.Builder()
			.putString("KEY_URL", url)
			.putString("KEY_FILENAME", name)
			.build()

		val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
			.setInputData(inputData)
			.addTag(taskId) // Attaching tag so we can observe this worker specifically
			.build()

		workManager.enqueue(downloadRequest)

		// Launching a coroutine to watch the WorkManager status flow stream
		viewModelScope.launch {
			workManager.getWorkInfoByIdFlow(downloadRequest.id).collect { workInfo ->
				if (workInfo != null) {
					processWorkUpdate(taskId, workInfo)
				}
			}
		}
	}

	private fun processWorkUpdate(taskId: String, workInfo: WorkInfo) {
		val index = rawTasksList.indexOfFirst { it.id == taskId }
		if (index == -1) return

		val currentProgress = workInfo.progress.getInt("KEY_PROGRESS", 0)

		val updatedStatus = when (workInfo.state) {
			WorkInfo.State.RUNNING -> DownloadStatus.DOWNLOADING
			WorkInfo.State.SUCCEEDED -> DownloadStatus.COMPLETED
			WorkInfo.State.FAILED -> DownloadStatus.FAILED
			else -> DownloadStatus.QUEUED
		}

		rawTasksList[index] = rawTasksList[index].copy(
			progress = currentProgress,
			status = updatedStatus
		)
		updateState()
	}

	private fun updateState() {
		_uiState.value =
			if (rawTasksList.isEmpty()) DownloadUiState.Empty else DownloadUiState.Active(rawTasksList.toList())
	}
}