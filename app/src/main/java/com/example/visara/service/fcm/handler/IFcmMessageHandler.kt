package com.example.visara.service.fcm.handler

import com.google.gson.JsonObject

interface IFcmMessageHandler {
    fun handle(dataJson: JsonObject)
}
