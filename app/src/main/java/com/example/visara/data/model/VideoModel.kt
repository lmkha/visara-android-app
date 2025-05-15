package com.example.visara.data.model

import android.net.Uri

data class VideoModel(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val userId: Long = 0L,
    val username: String = "",
    val userFullName: String = "",
    val userProfilePic: String = "",
    val playlistId: String? = null,
    val title: String = "",
    val description: String = "",
    val hashtags: List<String> = emptyList<String>(),
    val thumbnailUrl: String = "",
    val localThumbnailUri: Uri? = null,
    val viewsCount: Long = 0L,
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val isPrivate: Boolean = true,
    val isCommentOff: Boolean = true,
    val isUploaded: Boolean = false,
    val isProcessed: Boolean = false,
    val duration: Long = 0L,
)
