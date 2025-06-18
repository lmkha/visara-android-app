package com.example.visara.data.model

import com.example.visara.data.local.entity.NotificationEntity
import com.example.visara.data.remote.dto.NotificationData
import com.example.visara.di.gson
import com.example.visara.notification.NotificationType

data class NotificationModel(
    val localId: String,
    val remoteId: String,
    val senderUsername: String,
    val senderAvatarUrl: String?,
    val receiverUsername: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean,
    val data: NotificationData?,
) {
    fun toNotificationEntity() : NotificationEntity {
        return NotificationEntity(
            id = localId,
            remoteId = remoteId,
            senderUsername = senderUsername,
            senderAvatarUrl = senderAvatarUrl,
            receiverUsername = receiverUsername,
            message = message,
            type = type,
            isRead = isRead,
            rawData = data?.let { gson.toJson(it) },
        )
    }
}
