package com.example.visara.data.model

data class MessageModel(
    val content: String = "",
    val isMine: Boolean = true,
    val receivedTime: String = "",
    val senderUsername: String = "",
    val receiverUsername: String = "",
    val hadSeen: Boolean = false,
)
