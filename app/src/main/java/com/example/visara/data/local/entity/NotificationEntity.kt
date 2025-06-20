package com.example.visara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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
)
