package com.example.visara.data.model

import com.example.visara.notification.NotificationType
import com.google.gson.annotations.SerializedName

data class NotificationModel(
    val localId: String,
    val remoteId: String,
    val senderUsername: String,
    val senderAvatarUrl: String?,
    val receiverUsername: String,
    val message: String,
    val createdAt: String,
    val updatedAt: String,
    val type: NotificationType,
    val isRead: Boolean,
    val data: NotificationData?,
)

interface NotificationData

data class NewVideoProcessedNotificationData(
    val videoId: String,
    val videoTitle: String,
    val thumbnailUrl: String,
) : NotificationData

data class CommentLikeNotificationData(
    val videoId: String,
    val videoTitle: String,
    val thumbnailUrl: String,
    val commentId: String,
    @SerializedName("comment") val commentContent: String,
) : NotificationData

data class CommentOnVideoNotificationData(
    val videoId: String,
    val videoTitle: String,
    val thumbnailUrl: String,
    val commentId: String,
    @SerializedName("comment") val commentContent: String,
) : NotificationData

data class VideoLikeNotificationData(
    val videoId: String,
    val videoTitle: String,
    val thumbnailUrl: String,
) : NotificationData

data class NewChatMessageNotificationData(
    val senderUsername: String = "",
    val content: String = "",
    val senderAvatar: String = "",
) : NotificationData
