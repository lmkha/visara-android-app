package com.example.visara.worker

import com.example.visara.data.model.VideoModel
import kotlinx.serialization.Serializable

@Serializable
data class UploadVideoWorkerParams(
    val videoMetaData: VideoModel,
    val videoUri: String,
    val thumbnailUri: String?,
)
