package com.example.visara.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val videoId: String = "",
    val userId: Long = 0L,
    val username: String = "",
    @SerializedName("userFullname") val userFullName: String = "",
    val userAvatarLink: String = "",
    val content: String = "",
    val replyTo: String? = null,
    val likeCount: Long = 0L,
    val replyCount: Long = 0L,
    val isEdited: Boolean = false,
) {
    fun toCommentModel() {

    }
}

data class LikeCommentDto(
    val id: String = "",
    val commentId: String = "",
    val userId: Long = 0L,
)
