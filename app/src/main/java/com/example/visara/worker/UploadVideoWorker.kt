package com.example.visara.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.visara.MainActivity
import com.example.visara.data.repository.VideoRepository
import com.example.visara.notification.NotificationHelper
import com.example.visara.ui.navigation.Destination
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
            e.printStackTrace()
            return Result.failure()
        }

        val result = with(params) {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("request-type", "navigation")
                putExtra("route", "studio")
                putExtra("destination", gson.toJson(Destination.Studio))
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder: NotificationCompat.Builder = notificationHelper.createUploadingNewVideoNotificationBuilder()
                .setContentIntent(pendingIntent)

            val uploadResult = videoRepository.uploadVideoFile(
                videoMetaData = videoMetaData,
                videoUri = videoUri.toUri(),
                thumbnailUri = thumbnailUri?.toUri(),
                onProgressChange = { progress ->
                    if (progress < 100) {
                        val notification = notificationBuilder
                            .setProgress(100, progress, false)
                            .build()
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
