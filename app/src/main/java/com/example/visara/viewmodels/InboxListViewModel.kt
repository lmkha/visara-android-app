package com.example.visara.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.repository.NotificationRepository
import com.example.visara.notification.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InboxListViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<InboxListScreenUiState> = MutableStateFlow(InboxListScreenUiState())
    val uiState: StateFlow<InboxListScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = notificationRepository.getAllNotifications(0, 200)
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    updateUiState(it)
                }
            }
        }
    }

    private fun updateUiState(newNotifications: List<NotificationModel>) {
        val newFollowersNotifications = mutableListOf<NotificationModel>()
        val activityNotifications = mutableListOf<NotificationModel>()
        val studioNotifications = mutableListOf<NotificationModel>()
        newNotifications.forEach { notification ->
            when(notification.type) {
                NotificationType.VIDEO_UPLOAD_PROCESSED -> studioNotifications.add(notification)
                NotificationType.VIDEO_FILE_UPLOADED -> studioNotifications.add(notification)
                NotificationType.NEW_FOLLOWER -> newFollowersNotifications.add(notification)
                NotificationType.VIDEO_LIKED -> activityNotifications.add(notification)
                NotificationType.COMMENT_LIKED -> activityNotifications.add(notification)
                NotificationType.COMMENT_ON_VIDEO -> activityNotifications.add(notification)
                else -> {}
            }
        }
        _uiState.update {
            it.copy(
                newFollowersNotifications = newFollowersNotifications,
                activityNotifications = activityNotifications,
                studioNotifications = studioNotifications,
            )
        }
    }
}

data class InboxListScreenUiState(
    val newFollowersNotifications: List<NotificationModel> = emptyList(),
    val activityNotifications: List<NotificationModel> = emptyList(),
    val studioNotifications: List<NotificationModel> = emptyList(),
    val systemNotifications: List<NotificationModel> = emptyList(),
)
