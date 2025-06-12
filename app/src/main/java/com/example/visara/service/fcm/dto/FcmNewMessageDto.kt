package com.example.visara.service.fcm.dto

import com.example.visara.data.model.fcm.FcmNewMessageModel

data class FcmNewMessageDto(
    val senderUsername: String = "",
    val content: String = "",
    val senderAvatar: String = "",
) : FcmDataDto {
    fun toFcmNewMessageModel() = FcmNewMessageModel(
        senderUsername = senderUsername,
        content = content,
        senderAvatar = senderAvatar,
    )
}
