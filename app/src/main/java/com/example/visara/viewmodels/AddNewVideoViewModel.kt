package com.example.visara.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.visara.PlayerManager
import com.example.visara.utils.NetworkMonitor
import com.example.visara.data.model.PlaylistModel
import com.example.visara.data.model.UserModel
import com.example.visara.data.repository.PlaylistRepository
import com.example.visara.data.repository.UserRepository
import com.example.visara.data.repository.VideoRepository
import com.example.visara.ui.screens.add_new_video.components.enter_video_info.PrivacyState
import com.example.visara.worker.UploadVideoWorkerParams
import com.example.visara.worker.UploadVideoWorker
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewVideoViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val networkMonitor: NetworkMonitor,
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
    private val playlistRepository: PlaylistRepository,
    private val playerManager: PlayerManager,
    private val gson: Gson,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddNewVideoScreenUiState())
    val uiState = _uiState.asStateFlow()
    private val _eventChannel = Channel<AddNewVideoScreenEvent>()
    val eventFlow: Flow<AddNewVideoScreenEvent> = _eventChannel.receiveAsFlow()
    val mediaController: StateFlow<MediaController?> = playerManager.mediaControllerFlow

    init {
        observerNetworkState()
        observerCurrentUser()
    }

    private fun observerNetworkState() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _uiState.update { it.copy(isOnline = isOnline) }
            }
        }
    }

    private fun observerCurrentUser() {
        viewModelScope.launch {
            userRepository.currentUser.collect { currentUser->
                val playlists = currentUser?.let { playlistRepository.getAllPlaylistByUserId(it.id)}
                _uiState.update {
                    it.copy(
                        currentUser = currentUser,
                        allPlaylists = playlists ?: emptyList()
                    )
                }
            }
        }
    }

    fun playUriVideo(uri: Uri) {
        playerManager.playUri(uri)
    }

    fun postVideo(data: AddVideoSubmitData) {
        viewModelScope.launch {
            if (data.videoUri == null) return@launch
            val newVideoMetaData = videoRepository.uploadVideoMetaData(
                draftId = _uiState.value.draftData.localId,
                title = data.title,
                description = data.description,
                hashtags = data.hashtags,
                privacy = data.privacy.value,
                isAllowComment = data.isAllowComment,
                videoUri = data.videoUri,
                thumbnailUri = data.thumbnailUri,
                currentUser = _uiState.value.currentUser,
                playlists = data.playlists
            )

            if (newVideoMetaData == null) {
                _eventChannel.send(AddNewVideoScreenEvent.UploadNewVideoMetaDataFailure)
                return@launch
            }

            val params = UploadVideoWorkerParams(
                videoMetaData = newVideoMetaData,
                videoUri = data.videoUri.toString(),
                thumbnailUri = data.thumbnailUri.toString(),
            )
            val jsonParams = gson.toJson(params)
            val inputData = workDataOf(UploadVideoWorker.KEY to jsonParams)
            val request = OneTimeWorkRequestBuilder<UploadVideoWorker>()
                .setInputData(inputData)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            WorkManager.getInstance(appContext).enqueue(request)

            _eventChannel.send(AddNewVideoScreenEvent.UploadNewVideoMetaDataSuccess)
        }
    }

    fun draftVideoPost(data: AddVideoSubmitData) {
        viewModelScope.launch {
            if (data.videoUri == null) {
                _eventChannel.send(AddNewVideoScreenEvent.DraftVideoPostFailure)
                return@launch
            }

            val result = videoRepository.draftVideoPost(
                draftId = uiState.value.draftData.localId,
                title = data.title,
                description = data.description,
                hashtags = data.hashtags,
                playlists = data.playlists,
                privacy = data.privacy.value,
                isAllowComment = data.isAllowComment,
                videoUri = data.videoUri,
                thumbnailUri = data.thumbnailUri,
                currentUser = _uiState.value.currentUser,
            )

            if (result) _eventChannel.send(AddNewVideoScreenEvent.DraftVideoPostSuccess)
            else _eventChannel.send(AddNewVideoScreenEvent.DraftVideoPostFailure)
        }
    }

    fun prepareDraftData(localDraftVideoId: Long) {
        viewModelScope.launch {
            val draftData = videoRepository.getDraftVideoByLocalId(localDraftVideoId)
            if (draftData != null) {
                _uiState.update { it.copy(draftData = draftData) }
            } else {
                _uiState.update { it.copy(draftData = createInitialAddVideoSubmitData()) }
            }
        }
    }
}

data class AddNewVideoScreenUiState(
    val isOnline: Boolean = false,
    val currentUser: UserModel? = null,
    val allPlaylists: List<PlaylistModel> = emptyList(),
    val draftData: AddVideoSubmitData = createInitialAddVideoSubmitData()
)

sealed class AddNewVideoScreenEvent {
    data object UploadNewVideoMetaDataSuccess: AddNewVideoScreenEvent()
    data object UploadNewVideoMetaDataFailure: AddNewVideoScreenEvent()
    data object DraftVideoPostSuccess: AddNewVideoScreenEvent()
    data object DraftVideoPostFailure: AddNewVideoScreenEvent()
}

data class AddVideoSubmitData(
    val localId: Long? = null,
    val title: String,
    val description: String,
    val playlists: List<PlaylistModel>,
    val hashtags: List<String>,
    val privacy: PrivacyState,
    val isAllowComment: Boolean,
    val thumbnailUri: Uri?,
    val videoUri: Uri? = null,
)

private fun createInitialAddVideoSubmitData() : AddVideoSubmitData {
    return AddVideoSubmitData(
        title = "",
        description = "",
        playlists = emptyList(),
        hashtags = emptyList(),
        privacy = PrivacyState.ALL,
        isAllowComment = true,
        videoUri = null,
        thumbnailUri = null,
        localId = null,
    )
}
