package com.example.visara.worker

import com.example.visara.data.model.VideoModel

data class UploadVideoWorkerParams(
    val videoMetaData: VideoModel,
    val videoUri: String,
    val thumbnailUri: String?,
)
