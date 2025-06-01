package com.example.visara.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.common.AppScope
import com.example.visara.data.model.VideoPrivacy
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewVideoViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val videoDetailRepository: VideoDetailRepository,
) : ViewModel() {
    val manager get() = videoDetailRepository.videoPlayerManager
    fun postVideo(
        videoUri: Uri?,
        thumbnailUri: Uri?,
        title: String,
        description: String,
        hashtags: List<String>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
        onUploadVideoMetadataSuccess: () -> Unit,
    ) {
        AppScope.launch {
            videoRepository.postVideo(
                videoUri = videoUri,
                thumbnailUri = thumbnailUri,
                title = title,
                description = description,
                hashtags = hashtags,
                privacy = privacy,
                isAllowComment = isAllowComment,
                onUploadVideoMetaDataSuccess = {
                    onNavigateToStudio(onUploadVideoMetadataSuccess)
                },
            )
        }
    }

    private fun onNavigateToStudio(callback: () -> Unit) {
        viewModelScope.launch {
            callback()
        }
    }
}
