package com.example.visara.service.fcm.dto

data class NewVideoProcessedFcmDto(
    val videoId: String,
    val videoTitle: String,
    val thumbnailUrl: String,
) : FcmDataDto
