package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.local.entity.VideoStatus
import com.example.visara.data.model.VideoModel
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
            val currentUser = userRepository.currentUser.first()
            val currentUsername = currentUser?.username
            val currentUserId = currentUser?.id
            if (currentUsername != null && currentUserId != null) {
                val activeVideoDeferred = async { videoRepository.getAllVideoByUserId(currentUserId) }
                videoRepository.getAllLocalVideoEntityFlow(currentUsername)
                    .collectLatest { allVideoEntity ->
                        val processingVideos = mutableListOf<VideoModel>()
                        val uploadingVideos = mutableListOf<VideoModel>()
                        val draftVideos = mutableListOf<VideoModel>()
                        val pendingReUploadVideos = mutableListOf<VideoModel>()

                        allVideoEntity.forEach { videoEntity ->
                            when (videoEntity.statusCode) {
                                VideoStatus.PROCESSING.code -> {
                                    processingVideos.add(videoRepository.convertVideoEntityToModel(videoEntity))
                                }
                                VideoStatus.UPLOADING.code -> {
                                    uploadingVideos.add(videoRepository.convertVideoEntityToModel(videoEntity))
                                }
                                VideoStatus.DRAFT.code -> {
                                    draftVideos.add(videoRepository.convertVideoEntityToModel(videoEntity))
                                }
                                VideoStatus.PENDING_RE_UPLOAD.code -> {
                                    pendingReUploadVideos.add(videoRepository.convertVideoEntityToModel(videoEntity))
                                }
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

    fun deleteDraftVideo(localId: Long) {
        viewModelScope.launch {
            videoRepository.deleteLocalVideoEntityByLocalId(localId)
        }
    }

    fun deletePendingReUploadVideo(localId: Long) {
        viewModelScope.launch {
            val videoEntity = videoRepository.getLocalVideoEntityById(localId)
            videoEntity?.remoteId?.let { videoRepository.deleteVideo(it) }
            videoRepository.deleteLocalVideoEntityByLocalId(localId)
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
