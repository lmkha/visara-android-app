package com.example.visara.data.remote.dto

import com.example.visara.data.model.NotificationModel
import com.example.visara.di.gson
import com.example.visara.service.fcm.NotificationProcessor
import com.example.visara.service.fcm.RemoteNotificationType
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import java.util.UUID

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
) {
    private fun determineType(typeString: String) : RemoteNotificationType {
        val type =  RemoteNotificationType.entries.find { it.remoteTypeString == typeString }
        return type ?: RemoteNotificationType.UNKNOWN
    }

    fun decode() : DecodedNotificationDto {
        val type = determineType(type)
        val data: NotificationData? = when (type) {
            RemoteNotificationType.VIDEO_UPLOAD_PROCESSED -> gson.fromJson(dataJsonObject, NewVideoProcessedNotificationData::class.java)
            RemoteNotificationType.VIDEO_LIKED -> gson.fromJson(dataJsonObject, VideoLikeNotificationData::class.java)
            RemoteNotificationType.COMMENT_LIKED -> gson.fromJson(dataJsonObject, CommentLikeNotificationData::class.java)
            RemoteNotificationType.COMMENT_ON_VIDEO -> gson.fromJson(dataJsonObject, CommentOnVideoNotificationData::class.java)
            RemoteNotificationType.NEW_FOLLOWER -> null
            RemoteNotificationType.NEW_MESSAGE -> null
            RemoteNotificationType.UNKNOWN -> null
        }
        return DecodedNotificationDto(
            id = id,
            senderUsername = senderUsername,
            senderAvatarUrl = senderAvatarUrl,
            receiverUsername = receiverUsername,
            message = message,
            type = type,
            isRead = isRead,
            createdAt = createdAt,
            updatedAt = updatedAt,
            data = data,
        )
    }
}

data class DecodedNotificationDto(
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
) {
    fun toNotificationModel() : NotificationModel {
        val localId = UUID.nameUUIDFromBytes(id.toByteArray()).toString()
        return NotificationModel(
            remoteId = id,
            localId = localId,
            senderUsername = senderUsername,
            senderAvatarUrl = senderAvatarUrl,
            receiverUsername = receiverUsername,
            message = message,
            type = type.toNotificationType(),
            isRead = isRead,
            data = data
        )
    }
}

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
