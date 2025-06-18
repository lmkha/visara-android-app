package com.example.visara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.remote.dto.CommentLikeNotificationData
import com.example.visara.data.remote.dto.CommentOnVideoNotificationData
import com.example.visara.data.remote.dto.NewChatMessageNotificationData
import com.example.visara.data.remote.dto.NewVideoProcessedNotificationData
import com.example.visara.data.remote.dto.NotificationData
import com.example.visara.data.remote.dto.VideoLikeNotificationData
import com.example.visara.di.gson
import com.example.visara.notification.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val remoteId: String,
    val senderUsername: String,
    val senderAvatarUrl: String?,
    val receiverUsername: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean,
    val rawData: String?,
) {
    fun toNotificationModel() : NotificationModel {
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
