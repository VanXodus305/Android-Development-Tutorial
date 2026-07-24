package com.example.myapplication

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class DownloadWorker(
	context: Context,
	workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

	override suspend fun doWork(): Result {
		val downloadUrl = inputData.getString("KEY_URL") ?: return Result.failure()
		val fileName = inputData.getString("KEY_FILENAME") ?: "downloaded_file.txt"

		return withContext(Dispatchers.IO) {
			try {
				// Simulating an active chunked HTTP network connection pipeline
				// In a production app, use URL(downloadUrl).openConnection() streaming bytes here
				for (progress in 1..100) {
					delay(50.milliseconds) // Simulating network transfer latency speeds

					// Emitting intermediate progress updates to the active WorkManager system database
					setProgress(workDataOf("KEY_PROGRESS" to progress))
				}

				Result.success()
			} catch (e: Exception) {
				Result.failure()
			}
		}
	}
}