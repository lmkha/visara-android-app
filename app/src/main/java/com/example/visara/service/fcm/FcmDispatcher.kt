package com.example.visara.service.fcm

import android.util.Log
import com.example.visara.data.repository.InboxRepository
import com.example.visara.service.fcm.FcmMessageType
import com.example.visara.service.fcm.dto.FcmNewMessageDto
import com.example.visara.service.fcm.dto.IFcmMessageDto
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmDispatcher @Inject constructor(
    private val inboxRepository: InboxRepository,
    private val gson: Gson,
) {
    fun dispatch(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.i("CHECK_VAR", "fcm data: ${remoteMessage.data}")
            val type = determineType(remoteMessage)
            if (type == FcmMessageType.UNKNOWN) return
            when (type) {
                FcmMessageType.NEW_MESSAGE -> {
                    parseMessageData<FcmNewMessageDto>(remoteMessage)?.let {
                        inboxRepository.receiveFcmMessage(it)
                    }
                }
                FcmMessageType.VIDEO_LIKED -> {
                }
                FcmMessageType.NEW_FOLLOWER -> {

                }
                FcmMessageType.COMMENT_LIKED -> {

                }
                FcmMessageType.COMMENT_ON_VIDEO -> {

                }
                FcmMessageType.VIDEO_UPLOAD_PROCESSED -> {

                }
                else -> {}
            }
        }
    }

    private fun determineType(remoteMessage: RemoteMessage) : FcmMessageType {
        val type= remoteMessage.data["type"] ?: return FcmMessageType.UNKNOWN
        return when (type) {
            "new_message" -> FcmMessageType.NEW_MESSAGE
            else -> FcmMessageType.UNKNOWN
        }
    }

    private inline fun <reified T : IFcmMessageDto> parseMessageData(remoteMessage: RemoteMessage): T? {
        val dataJson = remoteMessage.data["data"] ?: return null
        return try {
            gson.fromJson(dataJson, T::class.java)
        } catch (_: Exception) {
            null
        }
    }
}