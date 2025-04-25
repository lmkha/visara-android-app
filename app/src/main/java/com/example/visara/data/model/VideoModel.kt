package com.example.visara.data.model

import com.google.gson.annotations.SerializedName

data class VideoModel(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val userId: Long = 0L,
    val playlistId: String? = null,
    val title: String = "",
    val description: String = "",
    val hashtags: List<String> = emptyList<String>(),
    val thumbnailUrl: String = "",
    val viewsCount: Long = 0L,
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val isPrivate: Boolean = true,
    val isCommentOff: Boolean = true,
    val isUploaded: Boolean = false,
    val isProcessed: Boolean = false
)
