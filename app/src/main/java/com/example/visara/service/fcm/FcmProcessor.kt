package com.example.visara.service.fcm

import android.util.Log
import com.example.visara.di.gson
import com.example.visara.service.fcm.dto.FcmResponseDto
import com.example.visara.service.fcm.handler.FcmCommentLikeHandler
import com.example.visara.service.fcm.handler.FcmCommentOnVideoHandler
import com.example.visara.service.fcm.handler.FcmNewChatMessageHandler
import com.example.visara.service.fcm.handler.FcmNewFollowerHandler
import com.example.visara.service.fcm.handler.FcmNewVideoProcessedHandler
import com.example.visara.service.fcm.handler.FcmVideoLikeHandler
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmProcessor @Inject constructor(
    private val fcmNewVideoProcessedHandler: FcmNewVideoProcessedHandler,
    private val fcmNewFollowerHandler: FcmNewFollowerHandler,
    private val fcmVideoLikeHandler: FcmVideoLikeHandler,
    private val fcmCommentLikeProcessedHandler: FcmCommentLikeHandler,
    private val fcmCommentOnVideoHandler: FcmCommentOnVideoHandler,
    private val fcmNewChatMessageHandler: FcmNewChatMessageHandler,
) {
    fun process(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            try {
                val content = remoteMessage.data["content"]
                val contentType = object : TypeToken<FcmResponseDto>() {}.type
                val decodedContent: FcmResponseDto = gson.fromJson(content, contentType)
                val type = determineType(decodedContent.type)
                dispatchMessage(type = type, dataJson = decodedContent.notiMetadata)

            } catch (e: Exception) {
                Log.e("CHECK_VAR", "error when process fcm message, error = ${e.toString()}")
            }
        }
    }

    private fun determineType(typeString: String) : FcmMessageType {
        val type =  FcmMessageType.entries.find { it.remoteTypeString == typeString }
        return type ?: FcmMessageType.UNKNOWN
    }

    private fun dispatchMessage(type: FcmMessageType, dataJson: JsonObject) {
        when (type) {
            FcmMessageType.VIDEO_UPLOAD_PROCESSED -> {
                fcmNewVideoProcessedHandler.handle(dataJson)
            }
            FcmMessageType.NEW_FOLLOWER -> {
                fcmNewFollowerHandler.handle(dataJson)
            }
            FcmMessageType.VIDEO_LIKED -> {
                fcmVideoLikeHandler.handle(dataJson)
            }
            FcmMessageType.COMMENT_LIKED -> {
                fcmCommentLikeProcessedHandler.handle(dataJson)
            }
            FcmMessageType.COMMENT_ON_VIDEO -> {
                fcmCommentOnVideoHandler.handle(dataJson)
            }
            FcmMessageType.NEW_MESSAGE -> {
                fcmNewChatMessageHandler.handle(dataJson)
            }
            FcmMessageType.UNKNOWN -> {
                Log.e("CHECK_VAR", "Received unknown type FCM message")
            }
        }
    }
}
