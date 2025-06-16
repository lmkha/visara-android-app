package com.example.visara.service.fcm.handlers

import com.example.visara.service.fcm.dto.FcmContent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmUnknownTypeHandler @Inject constructor() : IHandleFcmMessageStrategy {
    override fun handle(content: FcmContent) {

    }
}