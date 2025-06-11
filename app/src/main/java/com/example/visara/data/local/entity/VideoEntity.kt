package com.example.visara.data.local.entity

import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.visara.data.model.PlaylistModel
import com.example.visara.data.model.VideoModel
import com.example.visara.di.gson
import com.example.visara.ui.screens.add_new_video.components.enter_video_info.PrivacyState
import com.example.visara.viewmodels.AddVideoSubmitData
import com.google.gson.reflect.TypeToken

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
) {
    fun toAddVideoSubmitData() : AddVideoSubmitData {
        val playlistsType = object : TypeToken<List<PlaylistModel>>() {}.type
        val hashtagsType = object : TypeToken<List<String>>() {}.type
        val playlists: List<PlaylistModel> = gson.fromJson(this.playlistsJson, playlistsType)
        val hashtags: List<String> = gson.fromJson(this.hashtagsJson, hashtagsType)

        return AddVideoSubmitData(
            localId = localId,
            title = title,
            description = description,
            playlists = playlists,
            hashtags = hashtags,
            privacy = PrivacyState.ALL,
            isAllowComment = !isCommentOff,
            thumbnailUri = localThumbnailUriString?.toUri(),
            videoUri = localVideoUriString?.toUri(),
        )
    }

    fun toVideoModel() : VideoModel {
        return VideoModel(
            localId = localId,
            username = username,
            userId = userId,
            userProfilePic = userProfilePic,
            userFullName = userFullName,
            title = title,
            description = description,
            duration = duration,
            isPrivate = isPrivate,
            isCommentOff = isCommentOff,
            localVideoUri = localVideoUriString?.toUri(),
            localThumbnailUri = localThumbnailUriString?.toUri()
        )
    }
}

enum class LocalVideoStatus(val code: Int) {
    ACTIVE(code = 0),
    PROCESSING(code = 1),
    UPLOADING(code = 2),
    DRAFT(code = 3),
    PENDING_RE_UPLOAD(code = 4)
}
