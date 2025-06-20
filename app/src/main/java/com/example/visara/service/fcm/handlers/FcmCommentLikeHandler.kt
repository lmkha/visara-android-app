package com.example.visara.service.fcm.handlers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.visara.MainActivity
import com.example.visara.data.remote.dto.DeserializedNotificationDto
import com.example.visara.notification.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmCommentLikeHandler @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val notificationHelper: NotificationHelper,
    private val notificationManager: NotificationManager,
) : HandleFcmMessageStrategy() {
    override fun showNotification(decodedNotificationDto: DeserializedNotificationDto) {
        val successIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val successPendingIntent: PendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            successIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = notificationHelper.createMyCommentLikedNotificationBuilder(decodedNotificationDto)
            .setContentIntent(successPendingIntent)
            .build()

        notificationManager.notify(decodedNotificationDto.id.hashCode(), notification)
    }

}
