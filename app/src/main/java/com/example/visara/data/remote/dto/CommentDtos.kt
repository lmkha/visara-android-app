package com.example.visara.data.remote.dto

import com.example.visara.data.model.CommentModel


data class CommentDto(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val videoId: String = "",
    val userId: Long = 0L,
    val username: String = "",
    val userFullname: String = "",
    val userProfilePic: String = "",
    val content: String = "",
    val replyTo: String? = null,
    val likeCount: Long = 0L,
    val replyCount: Long = 0L,
    val isEdited: Boolean = false,
    val liked: Boolean = false,
) {
    fun toCommentModel() : CommentModel {
        return CommentModel(
            createdAt = createdAt,
            updatedAt = updatedAt,
            id = id,
            userId = userId,
            videoId = videoId,
            username = username,
            userFullName = userFullname,
            userAvatarUrl = userProfilePic,
            content = content,
            replyTo = replyTo,
            likeCount = likeCount,
            replyCount = replyCount,
            isEdited = isEdited,
            isLiked = liked,
        )
    }
}

data class LikeCommentDto(
    val id: String = "",
    val commentId: String = "",
    val userId: Long = 0L,
)
