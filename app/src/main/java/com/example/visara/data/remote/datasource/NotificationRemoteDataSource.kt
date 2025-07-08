package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.NotificationApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.NotificationDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRemoteDataSource @Inject constructor(
    private val notificationApi: NotificationApi,
    json: Json
) : RemoteDataSource(json) {
    suspend fun getNotifications(username: String, page: Int, size: Int) : ApiResult<List<NotificationDto>> {
        return callApi({ notificationApi.getAllNotifications(username = username, page = page, size = size) }) { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
            json.decodeFromString<ApiResponse<List<NotificationDto>>>(responseBody).toApiResult()
        }
    }
}
