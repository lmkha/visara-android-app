package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.local.entity.LocalVideoStatus
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<StudioScreenUiState> = MutableStateFlow(StudioScreenUiState())
    val uiState: StateFlow<StudioScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val currentUsername = userRepository.currentUser.first()?.username
            val currentUserId = userRepository.currentUser.first()?.id
            if (currentUsername != null && currentUserId != null) {
                val allLocalVideoEntity = videoRepository.getAllLocalVideoEntity(currentUsername)

                val activeVideoDeferred = async { videoRepository.getAllVideoByUserId(currentUserId) }
                val processingVideos = mutableListOf<VideoModel>()
                val uploadingVideos = mutableListOf<VideoModel>()
                val draftVideos = mutableListOf<VideoModel>()
                val pendingReUploadVideos = mutableListOf<VideoModel>()

                allLocalVideoEntity.forEach { videoEntity ->
                    when (videoEntity.statusCode) {
                        LocalVideoStatus.PROCESSING.code -> { processingVideos.add(videoEntity.toVideoModel()) }
                        LocalVideoStatus.UPLOADING.code -> { uploadingVideos.add(videoEntity.toVideoModel()) }
                        LocalVideoStatus.DRAFT.code -> { draftVideos.add(videoEntity.toVideoModel()) }
                        LocalVideoStatus.PENDING_RE_UPLOAD.code -> { pendingReUploadVideos.add(videoEntity.toVideoModel()) }
                    }
                }
                val activeVideos = activeVideoDeferred.await()

                _uiState.update {
                    it.copy(
                        activeVideos = activeVideos,
                        processingVideos = processingVideos,
                        uploadingVideos = uploadingVideos,
                        draftVideos = draftVideos,
                        pendingReUploadVideos = pendingReUploadVideos
                    )
                }
            }
        }
    }
}

data class StudioScreenUiState(
    val activeVideos: List<VideoModel> = emptyList(),
    val processingVideos: List<VideoModel> = emptyList(),
    val uploadingVideos: List<VideoModel> = emptyList(),
    val draftVideos: List<VideoModel> = emptyList(),
    val pendingReUploadVideos: List<VideoModel> = emptyList(),
)
