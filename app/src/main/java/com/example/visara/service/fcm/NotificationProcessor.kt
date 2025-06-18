package com.example.visara.service.fcm

import android.util.Log
import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.data.repository.NotificationRepository
import com.example.visara.di.gson
import com.example.visara.service.fcm.handlers.FcmCommentLikeHandler
import com.example.visara.service.fcm.handlers.FcmCommentOnVideoHandler
import com.example.visara.service.fcm.handlers.FcmNewChatMessageHandler
import com.example.visara.service.fcm.handlers.FcmNewFollowerHandler
import com.example.visara.service.fcm.handlers.FcmNewVideoProcessedHandler
import com.example.visara.service.fcm.handlers.FcmUnknownTypeHandler
import com.example.visara.service.fcm.handlers.FcmVideoLikeHandler
import com.example.visara.service.fcm.handlers.HandleFcmMessageStrategy
import com.google.firebase.messaging.RemoteMessage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationProcessor @Inject constructor(
    fcmNewVideoProcessedHandler: FcmNewVideoProcessedHandler,
    fcmNewFollowerHandler: FcmNewFollowerHandler,
    fcmVideoLikeHandler: FcmVideoLikeHandler,
    fcmCommentLikeHandler: FcmCommentLikeHandler,
    fcmCommentOnVideoHandler: FcmCommentOnVideoHandler,
    fcmNewChatMessageHandler: FcmNewChatMessageHandler,
    fcmUnknownTypeHandler: FcmUnknownTypeHandler,
    private val notificationRepository: NotificationRepository,
) {

    private val handlerMap: Map<RemoteNotificationType, HandleFcmMessageStrategy> = mapOf(
        RemoteNotificationType.VIDEO_UPLOAD_PROCESSED to fcmNewVideoProcessedHandler,
        RemoteNotificationType.NEW_FOLLOWER to fcmNewFollowerHandler,
        RemoteNotificationType.VIDEO_LIKED to fcmVideoLikeHandler,
        RemoteNotificationType.COMMENT_LIKED to fcmCommentLikeHandler,
        RemoteNotificationType.COMMENT_ON_VIDEO to fcmCommentOnVideoHandler,
        RemoteNotificationType.NEW_MESSAGE to fcmNewChatMessageHandler,
        RemoteNotificationType.UNKNOWN to fcmUnknownTypeHandler,
    )

    fun process(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isEmpty()) return
        try {
            val content: NotificationDto = gson.fromJson(remoteMessage.data["content"], NotificationDto::class.java)
            val type = determineType(content.type)
            if (type == RemoteNotificationType.UNKNOWN) {
                Log.e("CHECK_VAR", "Received unknown type fcm message.")
                return
            }
            val handler = getHandler(type) ?: return
            handler.handle(content)
            val decodedContent = content.decode()
            notificationRepository.saveNotification(decodedContent)
            handler.showNotification(decodedContent)

        } catch (e: Exception) {
            Log.e("CHECK_VAR", "error when process fcm message, error = $e")
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
