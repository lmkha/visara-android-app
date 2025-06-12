package com.example.visara.service.fcm.handler

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.visara.MainActivity
import com.example.visara.data.repository.VideoRepository
import com.example.visara.di.gson
import com.example.visara.notification.NotificationHelper
import com.example.visara.service.fcm.dto.NewVideoProcessedFcmDto
import com.example.visara.ui.navigation.Destination
import com.example.visara.ui.screens.studio.StudioSelectedTag
import com.google.gson.JsonObject
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
) : IFcmMessageHandler {
    override fun handle(dataJson: JsonObject) {
        CoroutineScope(Dispatchers.IO).launch {
            val newVideoProcessedDto: NewVideoProcessedFcmDto = gson.fromJson(dataJson, NewVideoProcessedFcmDto::class.java)

            val localVideoEntity = videoRepository.getLocalVideoEntityByTitle(newVideoProcessedDto.videoTitle)

            localVideoEntity?.let { videoRepository.deleteLocalVideoEntity(it) }

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
                    title = newVideoProcessedDto.videoTitle,
                    thumbnailUrl = newVideoProcessedDto.thumbnailUrl,
                )
                .setContentIntent(successPendingIntent)
                .build()

            notificationManager.notify(234, notification)
        }
    }
}
