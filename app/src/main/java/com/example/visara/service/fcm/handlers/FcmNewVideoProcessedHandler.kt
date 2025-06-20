package com.example.visara.service.fcm.handlers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.visara.MainActivity
import com.example.visara.data.remote.dto.DeserializedNotificationDto
import com.example.visara.data.remote.dto.NewVideoProcessedNotificationData
import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.data.repository.VideoRepository
import com.example.visara.notification.NotificationHelper
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.studio.StudioSelectedTag
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmNewVideoProcessedHandler @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val videoRepository: VideoRepository,
    private val notificationManager: NotificationManager,
    private val notificationHelper: NotificationHelper,
    private val gson: Gson,
) : HandleFcmMessageStrategy() {

    override fun handle(content: NotificationDto) {
        CoroutineScope(Dispatchers.IO).launch {
            val newVideoProcessedDto: NewVideoProcessedNotificationData = gson.fromJson(content.dataJsonObject, NewVideoProcessedNotificationData::class.java)
            val localVideoEntity = videoRepository.getLocalVideoEntityByTitle(newVideoProcessedDto.videoTitle)
            localVideoEntity?.let { videoRepository.deleteLocalVideoEntity(it) }
        }
    }

    override fun showNotification(decodedNotificationDto: DeserializedNotificationDto) {
        val notificationData = decodedNotificationDto.data as NewVideoProcessedNotificationData
        val successIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("request-type", "navigation")
            putExtra("route", "studio")
            putExtra(
                "destination",
                gson.toJson(Destination.Studio(selectedTagIndex = StudioSelectedTag.ACTIVE.ordinal))
            )
        }
        val successPendingIntent: PendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            successIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = notificationHelper
            .createVideoProcessedNotificationBuilder(
                title = notificationData.videoTitle,
                thumbnailUrl = notificationData.thumbnailUrl,
            )
            .setContentIntent(successPendingIntent)
            .build()

        notificationManager.notify(decodedNotificationDto.id.hashCode(), notification)
    }
}
