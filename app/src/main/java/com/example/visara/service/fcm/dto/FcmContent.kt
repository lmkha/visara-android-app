package com.example.visara.service.fcm.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class FcmContent(
    val fromUsername: String,
    val toUsername: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    @SerializedName("notiMetadata")
    val dataJsonObject: JsonObject?
)
