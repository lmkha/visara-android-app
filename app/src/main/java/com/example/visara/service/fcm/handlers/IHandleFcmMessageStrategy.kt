package com.example.visara.service.fcm.handlers

import com.example.visara.service.fcm.dto.FcmContent

interface IHandleFcmMessageStrategy {
    fun handle(content: FcmContent)
}
