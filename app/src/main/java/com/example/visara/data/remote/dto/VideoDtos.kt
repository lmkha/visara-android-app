package com.example.visara.data.remote.dto

import com.example.visara.data.model.VideoModel


data class VideoDto(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val userId: Long = 0L,
    val username: String = "",
    val userFullname: String = "",
    val userProfilePic: String = "",
    val playlistId: String? = null,
    val title: String = "",
    val description: String = "",
    val tags: List<String> = emptyList<String>(),
    val thumbnailUrl: String = "",
    val viewsCount: Long = 0L,
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val isPrivate: Boolean = true,
    val isCommentOff: Boolean = true,
    val isUploaded: Boolean = false,
    val isProcessed: Boolean = false,
    val duration: Long = 0L,
) {
    fun toVideoModel(): VideoModel {
        return VideoModel(
            createdAt = createdAt,
            updatedAt = updatedAt,
            id = id,
            userId = userId,
            username = username,
            userFullName = userFullname,
            userProfilePic = userProfilePic,
            playlistId = playlistId,
            title = title,
            description = description,
            hashtags = tags,
            thumbnailUrl = thumbnailUrl,
            viewsCount = viewsCount,
            likesCount = likesCount,
            commentsCount = commentsCount,
            isPrivate = isPrivate,
            isCommentOff = isCommentOff,
            isUploaded = isUploaded,
            isProcessed = isProcessed,
            duration = duration,
        )
    }
}
