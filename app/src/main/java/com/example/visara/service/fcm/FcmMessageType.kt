package com.example.visara.service.fcm

enum class FcmMessageType(val remoteTypeString: String?) {
    VIDEO_UPLOAD_PROCESSED(remoteTypeString = "NEW_VIDEO"),
    NEW_FOLLOWER(remoteTypeString = "NEW_FOLLOWER"),
    VIDEO_LIKED(remoteTypeString = "VIDEO_LIKE"),
    COMMENT_LIKED(remoteTypeString = "COMMENT_LIKED"),
    COMMENT_ON_VIDEO(remoteTypeString = "COMMENT_ON_VIDEO"),
    NEW_MESSAGE(remoteTypeString = "NEW_MESSAGE"),
    UNKNOWN(remoteTypeString = null),
}