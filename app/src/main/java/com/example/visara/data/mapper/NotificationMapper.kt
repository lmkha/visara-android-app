package com.example.visara.data.mapper

import com.example.visara.data.local.entity.NotificationEntity
import com.example.visara.data.model.CommentLikeNotificationData
import com.example.visara.data.model.CommentOnVideoNotificationData
import com.example.visara.data.model.NewChatMessageNotificationData
import com.example.visara.data.model.NewVideoProcessedNotificationData
import com.example.visara.data.model.NotificationData
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.model.VideoLikeNotificationData
import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.notification.NotificationType
import com.example.visara.service.fcm.RemoteNotificationType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationMapper @Inject constructor(private val json: Json) {
    fun toModel(notificationDto: NotificationDto) : NotificationModel {
        val type =  RemoteNotificationType.entries
            .find {it.remoteTypeString == notificationDto.type }?.toNotificationType()
            ?: RemoteNotificationType.UNKNOWN.toNotificationType()
        with(notificationDto) {
            val data = if (dataJsonObject != null) {
                when (type) {
                    NotificationType.VIDEO_UPLOAD_PROCESSED -> json.decodeFromJsonElement<NewVideoProcessedNotificationData>(dataJsonObject)
                    NotificationType.VIDEO_LIKED -> json.decodeFromJsonElement<VideoLikeNotificationData>(dataJsonObject)
                    NotificationType.COMMENT_LIKED -> json.decodeFromJsonElement<CommentLikeNotificationData>(dataJsonObject)
                    NotificationType.COMMENT_ON_VIDEO -> json.decodeFromJsonElement<CommentOnVideoNotificationData>(dataJsonObject)
                    else -> null
                }
            } else null

            val localId = UUID.nameUUIDFromBytes(id.toByteArray()).toString()
            return NotificationModel(
                remoteId = id,
                localId = localId,
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

    fun toModel(entity: NotificationEntity) : NotificationModel {
        with(entity) {
            val data: NotificationData? = rawData?.let {
                when (type) {
                    NotificationType.VIDEO_UPLOAD_PROCESSED -> json.decodeFromString<NewVideoProcessedNotificationData>(rawData)
                    NotificationType.VIDEO_LIKED -> json.decodeFromString<VideoLikeNotificationData>(rawData)
                    NotificationType.COMMENT_LIKED -> json.decodeFromString<CommentLikeNotificationData>(rawData)
                    NotificationType.COMMENT_ON_VIDEO -> json.decodeFromString<CommentOnVideoNotificationData>(rawData)
                    NotificationType.NEW_MESSAGE -> json.decodeFromString<NewChatMessageNotificationData>(rawData)
                    else -> null
                }
            }
            return NotificationModel(
                localId = id,
                remoteId = remoteId,
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

    fun toEntity(model: NotificationModel) : NotificationEntity {
        with(model) {
            return NotificationEntity(
                id = localId,
                remoteId = remoteId,
                senderUsername = senderUsername,
                senderAvatarUrl = senderAvatarUrl,
                receiverUsername = receiverUsername,
                message = message,
                type = type,
                isRead = isRead,
                createdAt = createdAt,
                updatedAt = updatedAt,
                rawData = data?.let { json.encodeToString(it) },
            )
        }
    }
}
