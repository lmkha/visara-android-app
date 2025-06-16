package com.example.visara.service.fcm

import android.util.Log
import com.example.visara.di.gson
import com.example.visara.service.fcm.handlers.FcmCommentLikeHandler
import com.example.visara.service.fcm.handlers.FcmCommentOnVideoHandler
import com.example.visara.service.fcm.handlers.FcmNewChatMessageHandler
import com.example.visara.service.fcm.handlers.FcmNewFollowerHandler
import com.example.visara.service.fcm.handlers.FcmNewVideoProcessedHandler
import com.example.visara.service.fcm.handlers.FcmUnknownTypeHandler
import com.example.visara.service.fcm.handlers.FcmVideoLikeHandler
import com.example.visara.service.fcm.handlers.IHandleFcmMessageStrategy
import com.example.visara.service.fcm.dto.FcmContent
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmProcessor @Inject constructor(
    fcmNewVideoProcessedHandler: FcmNewVideoProcessedHandler,
    fcmNewFollowerHandler: FcmNewFollowerHandler,
    fcmVideoLikeHandler: FcmVideoLikeHandler,
    fcmCommentLikeHandler: FcmCommentLikeHandler,
    fcmCommentOnVideoHandler: FcmCommentOnVideoHandler,
    fcmNewChatMessageHandler: FcmNewChatMessageHandler,
    fcmUnknownTypeHandler: FcmUnknownTypeHandler,
) {
    private val handlerMap: Map<FcmMessageType, IHandleFcmMessageStrategy> = mapOf(
        FcmMessageType.VIDEO_UPLOAD_PROCESSED to fcmNewVideoProcessedHandler,
        FcmMessageType.NEW_FOLLOWER to fcmNewFollowerHandler,
        FcmMessageType.VIDEO_LIKED to fcmVideoLikeHandler,
        FcmMessageType.COMMENT_LIKED to fcmCommentLikeHandler,
        FcmMessageType.COMMENT_ON_VIDEO to fcmCommentOnVideoHandler,
        FcmMessageType.NEW_MESSAGE to fcmNewChatMessageHandler,
        FcmMessageType.UNKNOWN to fcmUnknownTypeHandler,
    )
    fun process(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            try {
                val content = remoteMessage.data["content"]
                val contentType = object : TypeToken<FcmContent>() {}.type
                val decodedContent: FcmContent = gson.fromJson(content, contentType)
                val type = determineType(decodedContent.type)
                dispatchMessageToHandler(type = type, content = decodedContent)

            } catch (e: Exception) {
                Log.e("CHECK_VAR", "error when process fcm message, error = $e")
            }
        }
    }

    private fun determineType(typeString: String) : FcmMessageType {
        val type =  FcmMessageType.entries.find { it.remoteTypeString == typeString }
        return type ?: FcmMessageType.UNKNOWN
    }

    private fun dispatchMessageToHandler(type: FcmMessageType, content: FcmContent) {
        handlerMap[type]?.handle(content)
    }
}
