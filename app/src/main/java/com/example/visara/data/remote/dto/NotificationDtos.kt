package com.example.visara.data.remote.dto

import com.example.visara.service.fcm.RemoteNotificationType
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class NotificationDto(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("fromUsername") val senderUsername: String,
    @SerializedName("fromUserProfilePic") val senderAvatarUrl: String?,
    @SerializedName("toUsername") val receiverUsername: String,
    @SerializedName("notiMetadata") val dataJsonObject: JsonObject?,
    val message: String,
    val type: String,
    val isRead: Boolean,
)

data class DeserializedNotificationDto(
    val id: String,
    val senderUsername: String,
    val senderAvatarUrl: String?,
    val receiverUsername: String,
    val message: String,
    val type: RemoteNotificationType,
    val isRead: Boolean,
    val createdAt: String,
    val updatedAt: String,
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
