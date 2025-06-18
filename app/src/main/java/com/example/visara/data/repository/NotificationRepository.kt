package com.example.visara.data.repository

import android.util.Log
import com.example.visara.data.local.datasource.NotificationLocalDataSource
import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.NotificationRemoteDataSource
import com.example.visara.data.remote.dto.DecodedNotificationDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
) {
    fun saveNotification(content: DecodedNotificationDto) : Result<Unit> {
        return content.toNotificationModel().toNotificationEntity().let {
            notificationLocalDataSource.saveNotificationEntity(it)
        }
    }

    suspend fun getAllNotifications(page: Int, size: Int) : Result<List<NotificationModel>> {
        Log.d("CHECK_VAR", "")
        val result = userLocalDataSource.getCurrentUsername()?.let {
            val apiResult = notificationRemoteDataSource.getNotifications(
                username = it,
                page = page,
                size = size
            )
            Log.d("CHECK_VAR", "api result: $apiResult")
            apiResult
        }
        if (result == null) return Result.failure(Throwable("Current user is null."))
        if (result !is ApiResult.Success) return Result.failure(Throwable("Fail to get notifications."))
        return try {
            Result.success(result.data.map { it.decode().toNotificationModel() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
