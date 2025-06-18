package com.example.visara.notification

enum class NotificationType {
    VIDEO_UPLOAD_PROCESSED,  // Notification when a video you uploaded has been processed by the server
    VIDEO_FILE_UPLOADED,
    NEW_FOLLOWER,            // Notification when someone follows you
    VIDEO_LIKED,             // Notification when someone likes one of your videos
    COMMENT_LIKED,           // Notification when someone likes a comment you made
    COMMENT_ON_VIDEO,        // Notification when someone comments on one of your videos
    NEW_MESSAGE,             // Notification when you receive a new message
    UNKNOWN,
}
