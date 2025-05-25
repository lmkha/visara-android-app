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

    companion object {
        val all = listOf<NotificationChannelInfo>(
            Message,
        )
    }
}
