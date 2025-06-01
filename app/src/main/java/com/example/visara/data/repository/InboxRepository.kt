package com.example.visara.data.repository

import android.app.NotificationManager
import com.example.visara.common.AppLifecycleObserver
import com.example.visara.data.model.fcm.FcmNewMessageModel
import com.example.visara.notification.NotificationHelper
import com.example.visara.service.fcm.dto.FcmNewMessageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InboxRepository @Inject constructor(
    private val notificationManager: NotificationManager,
    private val notificationHelper: NotificationHelper,
    private val appLifecycleObserver: AppLifecycleObserver,
) {
    private val _newMessage: MutableStateFlow<FcmNewMessageModel?> = MutableStateFlow(null)
    val newMessage: StateFlow<FcmNewMessageModel?> = _newMessage.asStateFlow()
    private var currentInboxListScreenState = CurrentInboxListScreenState()

    fun receiveFcmMessage(messageDto: FcmNewMessageDto) {
        CoroutineScope(Dispatchers.IO).launch {
            val fcmNewMessageModel = messageDto.toFcmNewMessageModel()
            _newMessage.update { fcmNewMessageModel }
            val notification = notificationHelper.createMessageNotification(fcmNewMessageModel)
            val notificationId = fcmNewMessageModel.senderUsername.hashCode()
            if (!appLifecycleObserver.isAppInForeground) {
                notificationManager.notify(notificationId, notification)
            } else {
                if (fcmNewMessageModel.senderUsername != currentInboxListScreenState.partnerUsername) {
                    notificationManager.notify(notificationId, notification)
                }
            }
        }
    }

    fun setActiveChatPartner(username: String) {
        currentInboxListScreenState = currentInboxListScreenState.copy(partnerUsername = username)
    }

    fun clearActiveChatPartner() {
        currentInboxListScreenState = currentInboxListScreenState.copy(partnerUsername = null)
    }
}

private data class CurrentInboxListScreenState(
    val partnerUsername: String? = null,
)
