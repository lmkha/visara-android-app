package com.example.visara.service.fcm.dto

data class FcmNewVideoProcessedData(
    val videoId: String,
    val videoTitle: String,
    val thumbnailUrl: String,
) : FcmData
