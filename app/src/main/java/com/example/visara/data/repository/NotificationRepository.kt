package com.example.visara.data.repository

import com.example.visara.data.local.datasource.NotificationLocalDataSource
import com.example.visara.data.local.datasource.UserLocalDataSource
import com.example.visara.data.mapper.NotificationMapper
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.NotificationRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val notificationMapper: NotificationMapper,
) {
    fun saveNotification(model: NotificationModel) : Result<Unit> {
        val entity = notificationMapper.toEntity(model)
        return notificationLocalDataSource.saveNotificationEntity(entity)
    }

    suspend fun getAllNotifications(page: Int, size: Int) : Result<List<NotificationModel>> {
        val result = userLocalDataSource.getCurrentUsername()?.let {
            val apiResult = notificationRemoteDataSource.getNotifications(
                username = it,
                page = page,
                size = size
            )
            apiResult
        }
        if (result == null) return Result.failure(Throwable("Current user is null."))
        if (result !is ApiResult.NetworkResult.Success) return Result.failure(Throwable("Fail to get notifications."))
        return try {
            Result.success(
                result.data.map { notificationMapper.toModel(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
