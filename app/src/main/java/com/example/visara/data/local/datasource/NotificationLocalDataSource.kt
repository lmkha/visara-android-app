package com.example.visara.data.local.datasource

import com.example.visara.data.local.entity.NotificationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationLocalDataSource @Inject constructor(

) {
    fun saveNotificationEntity(notification: NotificationEntity) : Result<Unit> {
        return Result.success(Unit)
    }
}
