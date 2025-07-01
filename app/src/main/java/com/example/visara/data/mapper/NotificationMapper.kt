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
import com.google.gson.Gson
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationMapper @Inject constructor(private val gson: Gson) {
    fun toModel(notificationDto: NotificationDto) : NotificationModel {
        val type =  RemoteNotificationType.entries
            .find {it.remoteTypeString == notificationDto.type }?.toNotificationType()
            ?: RemoteNotificationType.UNKNOWN.toNotificationType()
        with(notificationDto) {
            val data: NotificationData? = when (type) {
                NotificationType.VIDEO_UPLOAD_PROCESSED -> gson.fromJson(dataJsonObject, NewVideoProcessedNotificationData::class.java)
                NotificationType.VIDEO_LIKED -> gson.fromJson(dataJsonObject, VideoLikeNotificationData::class.java)
                NotificationType.COMMENT_LIKED -> gson.fromJson(dataJsonObject, CommentLikeNotificationData::class.java)
                NotificationType.COMMENT_ON_VIDEO -> gson.fromJson(dataJsonObject, CommentOnVideoNotificationData::class.java)
                else -> null
            }
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
                    NotificationType.VIDEO_UPLOAD_PROCESSED -> gson.fromJson(rawData, NewVideoProcessedNotificationData::class.java)
                    NotificationType.VIDEO_LIKED -> gson.fromJson(rawData, VideoLikeNotificationData::class.java)
                    NotificationType.COMMENT_LIKED -> gson.fromJson(rawData, CommentLikeNotificationData::class.java)
                    NotificationType.COMMENT_ON_VIDEO -> gson.fromJson(rawData, CommentOnVideoNotificationData::class.java)
                    NotificationType.NEW_MESSAGE -> gson.fromJson(rawData, NewChatMessageNotificationData::class.java)
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
                rawData = data?.let { gson.toJson(it) },
            )
        }
    }
}
