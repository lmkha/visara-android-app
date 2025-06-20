package com.example.visara.data.repository

import com.example.visara.data.local.datasource.NotificationLocalDataSource
import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.local.entity.NotificationEntity
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.NotificationRemoteDataSource
import com.example.visara.data.remote.dto.CommentLikeNotificationData
import com.example.visara.data.remote.dto.CommentOnVideoNotificationData
import com.example.visara.data.remote.dto.DeserializedNotificationDto
import com.example.visara.data.remote.dto.NewChatMessageNotificationData
import com.example.visara.data.remote.dto.NewVideoProcessedNotificationData
import com.example.visara.data.remote.dto.NotificationData
import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.data.remote.dto.VideoLikeNotificationData
import com.example.visara.notification.NotificationType
import com.example.visara.service.fcm.RemoteNotificationType
import com.google.gson.Gson
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val gson: Gson,
) {
    fun saveNotification(content: DeserializedNotificationDto) : Result<Unit> {
        return convertNotificationModelToEntity(convertDeserializedNotificationDtoToModel(content)).let { entity ->
            notificationLocalDataSource.saveNotificationEntity(entity)
        }
    }

    suspend fun getAllNotifications(page: Int, size: Int) : Result<List<NotificationModel>> {
        val result = userLocalDataSource.getCurrentUsername()?.let {
            val apiResult = notificationRemoteDataSource.getNotifications(
                username = it,
                page = page,
                size = size
            )
            apiResult
        }
        if (result == null) return Result.failure(Throwable("Current user is null."))
        if (result !is ApiResult.Success) return Result.failure(Throwable("Fail to get notifications."))
        return try {
            Result.success(
                result.data.map {
                    convertDeserializedNotificationDtoToModel(deserializeNotificationDto(it))
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deserializeNotificationDto(notificationDto: NotificationDto) : DeserializedNotificationDto {
        val type =  RemoteNotificationType.entries
            .find {it.remoteTypeString == notificationDto.type }
            ?: RemoteNotificationType.UNKNOWN
        with(notificationDto) {
            val data: NotificationData? = when (type) {
                RemoteNotificationType.VIDEO_UPLOAD_PROCESSED -> gson.fromJson(dataJsonObject, NewVideoProcessedNotificationData::class.java)
                RemoteNotificationType.VIDEO_LIKED -> gson.fromJson(dataJsonObject, VideoLikeNotificationData::class.java)
                RemoteNotificationType.COMMENT_LIKED -> gson.fromJson(dataJsonObject, CommentLikeNotificationData::class.java)
                RemoteNotificationType.COMMENT_ON_VIDEO -> gson.fromJson(dataJsonObject, CommentOnVideoNotificationData::class.java)
                RemoteNotificationType.NEW_FOLLOWER -> null
                RemoteNotificationType.NEW_MESSAGE -> null
                RemoteNotificationType.UNKNOWN -> null
            }
            return DeserializedNotificationDto(
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

    fun convertNotificationEntityToModel(entity: NotificationEntity) : NotificationModel {
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
                data = data,
            )
        }
    }

    private fun convertNotificationModelToEntity(model: NotificationModel) : NotificationEntity {
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
                rawData = data?.let { gson.toJson(it) },
            )        }
    }

    private fun convertDeserializedNotificationDtoToModel(dto: DeserializedNotificationDto) : NotificationModel {
        with(dto) {
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
}
