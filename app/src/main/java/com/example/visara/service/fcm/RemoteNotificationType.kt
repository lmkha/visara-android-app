package com.example.visara.service.fcm

import com.example.visara.notification.NotificationType

enum class RemoteNotificationType(val remoteTypeString: String?) {
    VIDEO_UPLOAD_PROCESSED(remoteTypeString = "NEW_VIDEO"),
    NEW_FOLLOWER(remoteTypeString = "FOLLOW"),
    VIDEO_LIKED(remoteTypeString = "LIKE_VIDEO"),
    COMMENT_LIKED(remoteTypeString = "LIKE_COMMENT"),
    COMMENT_ON_VIDEO(remoteTypeString = "COMMENT_ON_VIDEO"),
    NEW_MESSAGE(remoteTypeString = "NEW_MESSAGE"),
    UNKNOWN(remoteTypeString = null);

    fun toNotificationType() : NotificationType {
        return when (this) {
            VIDEO_UPLOAD_PROCESSED -> NotificationType.VIDEO_UPLOAD_PROCESSED
            NEW_FOLLOWER -> NotificationType.NEW_FOLLOWER
            VIDEO_LIKED -> NotificationType.VIDEO_LIKED
            COMMENT_LIKED -> NotificationType.COMMENT_LIKED
            COMMENT_ON_VIDEO -> NotificationType.COMMENT_ON_VIDEO
            NEW_MESSAGE -> NotificationType.NEW_MESSAGE
            UNKNOWN -> NotificationType.UNKNOWN
        }
    }
}
