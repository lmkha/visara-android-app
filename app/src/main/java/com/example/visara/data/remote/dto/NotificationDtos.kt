package com.example.visara.data.remote.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class NotificationDto(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("fromUsername") val senderUsername: String,
    @SerializedName("fromUserProfilePic") val senderAvatarUrl: String?,
    @SerializedName("toUsername") val receiverUsername: String,
    @SerializedName("notiMetadata") val dataJsonObject: JsonObject?,
    val message: String,
    val type: String,
    val isRead: Boolean,
)
