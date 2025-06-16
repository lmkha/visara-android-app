package com.example.visara.service.fcm.dto

import com.example.visara.data.model.fcm.FcmNewMessageModel

data class FcmNewMessageData(
    val senderUsername: String = "",
    val content: String = "",
    val senderAvatar: String = "",
) : FcmData {
    fun toFcmNewMessageModel() = FcmNewMessageModel(
        senderUsername = senderUsername,
        content = content,
        senderAvatar = senderAvatar,
    )
}
