package com.example.visara.service.fcm.handlers

import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.data.remote.dto.DecodedNotificationDto

abstract class HandleFcmMessageStrategy {
    open fun handle(content: NotificationDto) { }

    open fun showNotification(decodedNotificationDto: DecodedNotificationDto) { }
}
