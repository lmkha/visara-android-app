package com.example.visara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class LocalVideoEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val remoteId: String = "",
    val userId: Long = 0L,
    val username: String = "",
    val userFullName: String = "",
    val userProfilePic: String = "",
    val playlistsJson: String = "",
    val title: String = "",
    val description: String = "",
    val hashtagsJson: String = "",
    val localThumbnailUriString: String? = null,
    val localVideoUriString: String? = null,
    val duration: Long = 0L,
    val isPrivate: Boolean = true,
    val isCommentOff: Boolean = true,
    val statusCode: Int = 0,
)

enum class VideoStatus(val code: Int) {
    ACTIVE(code = 0),
    PROCESSING(code = 1),
    UPLOADING(code = 2),
    DRAFT(code = 3),
    PENDING_RE_UPLOAD(code = 4)
}
