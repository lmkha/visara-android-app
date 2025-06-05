package com.example.visara.worker

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.visara.data.repository.VideoRepository
import com.example.visara.notification.NotificationHelper
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadVideoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: NotificationManager,
    private val notificationHelper: NotificationHelper,
    private val videoRepository: VideoRepository,
    private val gson: Gson,
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val KEY: String = "params"
        private const val NOTIFICATION_ID = 2712
    }

    override suspend fun doWork(): Result {
        val jsonParams = inputData.getString(KEY) ?: return Result.failure()
        val params = try {
            gson.fromJson(jsonParams, UploadVideoWorkerParams::class.java) ?: return Result.failure()
        } catch (e : Exception) {
            Log.e("UploadVideoWorker", "Error deserializing JSON: ${e.message}")
            return Result.failure()
        }

        val result = with(params) {
            val uploadResult = videoRepository.uploadVideo(
                videoMetaData = videoMetaData,
                videoUri = videoUri.toUri(),
                thumbnailUri = thumbnailUri?.toUri(),
                onProgressChange = { progress ->
                    if (progress < 100) {
                        val notification = notificationHelper.createUploadingNewVideoNotification(
                            currentProgress = progress,
                            maxProgress = 100
                        )
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    } else {
                        notificationManager.cancel(NOTIFICATION_ID)
                    }
                }
            )
            return@with uploadResult
        }
        return if (result) Result.success() else Result.failure()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = notificationHelper.createUploadingNewVideoNotification(
            currentProgress = 0,
            maxProgress = 100
        )

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }
}
