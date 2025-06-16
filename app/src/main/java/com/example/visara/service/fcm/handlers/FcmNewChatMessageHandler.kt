package com.example.visara.service.fcm.handlers

import com.example.visara.service.fcm.dto.FcmContent
import javax.inject.Inject

class FcmNewChatMessageHandler @Inject constructor(

) : IHandleFcmMessageStrategy {
    override fun handle(content: FcmContent) {

    }
}
