package com.example.visara.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.repository.NotificationRepository
import com.example.visara.notification.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityInboxViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ActivityInboxScreenUiState())
    val uiState = _uiState.asStateFlow()

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
        val activityNotifications = mutableListOf<NotificationModel>()
        newNotifications.forEach { notification ->
            when(notification.type) {
                NotificationType.VIDEO_LIKED -> activityNotifications.add(notification)
                NotificationType.COMMENT_LIKED -> activityNotifications.add(notification)
                NotificationType.COMMENT_ON_VIDEO -> activityNotifications.add(notification)
                else -> {}
            }
        }
        _uiState.update {
            it.copy(
                activityNotifications = activityNotifications,
            )
        }
    }
}
data class ActivityInboxScreenUiState(
    val activityNotifications: List<NotificationModel> = emptyList(),
)
