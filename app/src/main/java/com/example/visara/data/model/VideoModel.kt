package com.example.visara.data.model

import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class VideoModel(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: String = "",
    val localId: Long? = null,
    val userId: Long = 0L,
    val username: String = "",
    val userFullName: String = "",
    val userProfilePic: String = "",
    val playlistIds: List<String> = emptyList(),
    val title: String = "",
    val description: String = "",
    val hashtags: List<String> = emptyList(),
    val thumbnailUrl: String = "",
    val viewsCount: Long = 0L,
    val likesCount: Long = 0L,
    val commentsCount: Long = 0L,
    val isPrivate: Boolean = true,
    val isCommentOff: Boolean = true,
    val isUploaded: Boolean = false,
    val isProcessed: Boolean = false,
    val duration: Long = 0L,
    @Transient
    val localThumbnailUri: Uri? = null,
    @Transient
    val localVideoUri: Uri? = null,
)
