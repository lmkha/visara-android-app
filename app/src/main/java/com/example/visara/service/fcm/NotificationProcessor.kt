package com.example.visara.service.fcm

import com.example.visara.data.mapper.NotificationMapper
import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.data.repository.NotificationRepository
import com.example.visara.service.fcm.handlers.HandleFcmMessageStrategy
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationProcessor @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val handlerMap: Map<RemoteNotificationType, @JvmSuppressWildcards HandleFcmMessageStrategy?>,
    private val notificationMapper: NotificationMapper,
    private val json: Json,
) {
    fun process(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isEmpty()) return
        try {
            val content = remoteMessage.data["content"]?.let { json.decodeFromString<NotificationDto>(it)} ?: return
            val type = determineType(content.type)
            val handler = getHandler(type) ?: return
            handler.handle(content)
            if (type == RemoteNotificationType.UNKNOWN) return
            val notificationModel = notificationMapper.toModel(content)
            notificationRepository.saveNotification(notificationModel)
            handler.showNotification(notificationModel)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getHandler(type: RemoteNotificationType) : HandleFcmMessageStrategy? {
        return handlerMap[type]
    }

    private fun determineType(typeString: String) : RemoteNotificationType {
        val type =  RemoteNotificationType.entries.find { it.remoteTypeString == typeString }
        return type ?: RemoteNotificationType.UNKNOWN
    }
}
