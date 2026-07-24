package com.example.myapplication

data class DownloadTask(
	val id: String,
	val fileName: String,
	val url: String,
	val progress: Int = 0,
	val status: DownloadStatus = DownloadStatus.QUEUED
)

enum class DownloadStatus {
	QUEUED, DOWNLOADING, COMPLETED, FAILED
}

// Sealed class enforcing strict UI state rendering rules
sealed class DownloadUiState {
	object Empty : DownloadUiState()
	data class Active(val tasks: List<DownloadTask>) : DownloadUiState()
}

