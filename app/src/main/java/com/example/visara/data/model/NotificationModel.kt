package com.example.visara.data.model

import com.example.visara.data.remote.dto.NotificationData
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
)