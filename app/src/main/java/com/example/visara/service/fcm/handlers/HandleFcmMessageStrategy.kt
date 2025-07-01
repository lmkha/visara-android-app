package com.example.visara.service.fcm.handlers

import com.example.visara.data.model.NotificationModel
import com.example.visara.data.remote.dto.NotificationDto

abstract class HandleFcmMessageStrategy {
    open fun handle(content: NotificationDto) { }

    open fun showNotification(model: NotificationModel) { }
}
