package com.example.visara.data.model

data class CommentModel(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val videoId: String = "",
    val userId: Long = 0L,
    val username: String = "",
    val userFullName: String = "",
    val userAvatarUrl: String = "",
    val content: String = "",
    val replyTo: String? = null,
    val likeCount: Long = 0L,
    val replyCount: Long = 0L,
    val isEdited: Boolean = false,
    val isLiked: Boolean = false,
    val isAdding: Boolean = false,
)
