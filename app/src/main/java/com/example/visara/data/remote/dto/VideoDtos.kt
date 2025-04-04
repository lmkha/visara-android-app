package com.example.visara.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VideoDto(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val userId: Long = 0L,
    val playlistId: String? = null,
    val title: String = "",
    val description: String = "",
    @SerializedName("tags") val hashtags: List<String> = emptyList<String>(),
    val thumbnailUrl: String = "",
    @SerializedName("viewsCount") val views: Long = 0L,
    @SerializedName("likesCount") val likes: Long = 0L,
    @SerializedName("commentsCount") val comments: Long = 0L,
    val isPrivate: Boolean = true,
    val isCommentOff: Boolean = true,
    val isUploaded: Boolean = false,
    val isProcessed: Boolean = false,
)
