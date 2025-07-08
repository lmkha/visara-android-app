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
import com.example.visara.R
import com.example.visara.data.local.entity.VideoStatus
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.repository.VideoRepository
import com.example.visara.notification.NotificationHelper
import com.example.visara.ui.Destination
import com.example.visara.ui.screens.studio.StudioSelectedTag
import com.example.visara.utils.createBitmapFromLocalUri
import com.example.visara.utils.createVideoThumbFromLocalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class UploadVideoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: NotificationManager,
    private val notificationHelper: NotificationHelper,
    private val videoRepository: VideoRepository,
    private val json: Json,
) : CoroutineWorker(appContext, workerParams) {
    companion object {
        const val KEY: String = "params"
        private const val NOTIFICATION_ID = 2712
    }

    override suspend fun doWork(): Result {
        val jsonParams = inputData.getString(KEY) ?: return Result.failure()
        val params = try {
            json.decodeFromString<UploadVideoWorkerParams>(jsonParams)
        } catch (e : Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        return with(params) {
            val progressIntent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("request-type", "navigation")
                putExtra("route", "studio")
                putExtra("destination", json.encodeToString(Destination.Studio()))
            }
            val progressPendingIntent: PendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                progressIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notificationBuilder: NotificationCompat.Builder = notificationHelper
                .createUploadingNewVideoNotificationBuilder()
                .setContentIntent(progressPendingIntent)

            val uploadResult = videoRepository.uploadVideoFile(
                videoMetaData = videoMetaData,
                videoUri = videoUri.toUri(),
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

            when (uploadResult) {
                is ApiResult.Success -> {
                    val localVideoEntity = videoMetaData.localId?.let { videoRepository.getLocalVideoEntityById(it) }
                    localVideoEntity?.let {
                        videoRepository.updateLocalVideoEntity(
                            it.copy(
                                remoteId = videoMetaData.id,
                                statusCode = VideoStatus.PROCESSING.code,
                            )
                        )
                    }

                    val thumbnailUri = thumbnailUri?.toUri()
                    if (thumbnailUri != null && thumbnailUri != "null".toUri()) {
                        videoRepository.uploadThumbnailFile(
                            videoId = videoMetaData.id,
                            thumbnailUri = thumbnailUri,
                        )
                    }
                    val videoThumbnailBitmap = if (thumbnailUri != null && thumbnailUri != "null".toUri()) {
                        createBitmapFromLocalUri(applicationContext, thumbnailUri)
                    } else {
                        createVideoThumbFromLocalUri(applicationContext, videoUri.toUri())
                    }

                    val successIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("request-type", "navigation")
                        putExtra("route", "studio")
                        putExtra("destination", json.encodeToString(Destination.Studio(selectedTagIndex = StudioSelectedTag.PROCESSING.ordinal)))
                    }
                    val successPendingIntent: PendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        successIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val notification = notificationHelper
                        .createFinishUploadVideoFileNotificationBuilder(videoThumbnailBitmap)
                        .setContentIntent(successPendingIntent)
                        .build()

                    notificationManager.notify(NOTIFICATION_ID, notification)

                    Result.success()
                }
                else -> {

                    val localVideoEntity = videoMetaData.localId?.let { videoRepository.getLocalVideoEntityById(it) }
                    localVideoEntity?.let {
                        videoRepository.updateLocalVideoEntity(
                            it.copy(statusCode = VideoStatus.PENDING_RE_UPLOAD.code)
                        )
                    }

                    val thumbnailUri = thumbnailUri?.toUri()
                    if (thumbnailUri != null && thumbnailUri != "null".toUri()) {
                        videoRepository.uploadThumbnailFile(
                            videoId = videoMetaData.id,
                            thumbnailUri = thumbnailUri,
                        )
                    }
                    val videoThumbnailBitmap = if (thumbnailUri != null && thumbnailUri != "null".toUri()) {
                        createBitmapFromLocalUri(applicationContext, thumbnailUri)
                    } else {
                        createVideoThumbFromLocalUri(applicationContext, videoUri.toUri())
                    }

                    val failureIntent = Intent(applicationContext, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("request-type", "navigation")
                        putExtra("route", "studio")
                        putExtra("destination", json.encodeToString(Destination.Studio(selectedTagIndex = StudioSelectedTag.PENDING_RE_UPLOAD.ordinal)))
                    }
                    val failurePendingIntent: PendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        0,
                        failureIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    val notification = notificationHelper
                        .createUploadVideoErrorFileNotificationBuilder(videoThumbnailBitmap)
                        .setContentIntent(failurePendingIntent)
                        .addAction(R.drawable.restart_alt_24px, "Retry", failurePendingIntent)
                        .build()

                    notificationManager.notify(NOTIFICATION_ID, notification)

                    Result.failure()
                }
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = notificationHelper.createUploadingNewVideoNotification(
            currentProgress = 0,
            maxProgress = 100
        )

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }
}
