package com.example.visara.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.visara.data.model.VideoPrivacy
import com.example.visara.data.repository.VideoDetailRepository
import com.example.visara.data.repository.VideoRepository
import com.example.visara.worker.UploadVideoWorkerParams
import com.example.visara.worker.UploadVideoWorker
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewVideoViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val videoRepository: VideoRepository,
    private val videoDetailRepository: VideoDetailRepository,
    private val gson: Gson,
) : ViewModel() {
    private val _eventChannel: Channel<AddNewVideoScreenEvent> = Channel<AddNewVideoScreenEvent>()
    val eventFlow: Flow<AddNewVideoScreenEvent> = _eventChannel.receiveAsFlow()

    val manager get() = videoDetailRepository.videoPlayerManager

    fun postVideo(
        videoUri: Uri?,
        thumbnailUri: Uri?,
        title: String,
        description: String,
        hashtags: List<String>,
        privacy: VideoPrivacy,
        isAllowComment: Boolean,
    ) {
        viewModelScope.launch {
            if (videoUri == null) return@launch
            val newVideoMetaData = videoRepository.uploadVideoMetaData(
                title = title,
                description = description,
                hashtags = hashtags,
                privacy = privacy,
                isAllowComment = isAllowComment
            )

            if (newVideoMetaData == null) {
                _eventChannel.send(AddNewVideoScreenEvent.UploadNewVideoMetaDataFailure)
                return@launch
            }

            val params = UploadVideoWorkerParams(
                videoMetaData = newVideoMetaData,
                videoUri = videoUri.toString(),
                thumbnailUri = thumbnailUri.toString(),
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
}

sealed class AddNewVideoScreenEvent {
    object UploadNewVideoMetaDataSuccess: AddNewVideoScreenEvent()
    object UploadNewVideoMetaDataFailure: AddNewVideoScreenEvent()
}
