package com.example.visara.notification

import android.app.NotificationManager

sealed class NotificationChannelInfo(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int,
) {
    object Message : NotificationChannelInfo(
        id = "messages_channel",
        name = "Messages",
        description = "New message",
        importance = NotificationManager.IMPORTANCE_HIGH
    )

    object UploadingNewVideo : NotificationChannelInfo(
        id = "uploading_new_video_channel",
        name = "Upload video",
        description = "Uploading video",
        importance = NotificationManager.IMPORTANCE_LOW,
    )

    companion object {
        val all = listOf<NotificationChannelInfo>(
            Message,
            UploadingNewVideo,
        )
    }
}
