package com.example.visara.service.fcm.dto

import com.google.gson.JsonObject

data class FcmResponseDto(
    val fromUsername: String,
    val toUsername: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val notiMetadata: JsonObject
)
