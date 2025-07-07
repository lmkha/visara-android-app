package com.example.visara.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class NotificationDto(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    @SerialName("fromUsername") val senderUsername: String,
    @SerialName("fromUserProfilePic") val senderAvatarUrl: String?,
    @SerialName("toUsername") val receiverUsername: String,
    @SerialName("notiMetadata") val dataJsonObject: JsonObject?,
    val message: String,
    val type: String,
    val isRead: Boolean,
)
