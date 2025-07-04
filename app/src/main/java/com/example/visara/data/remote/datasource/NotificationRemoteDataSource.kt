package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.NotificationApi
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.NotificationDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRemoteDataSource @Inject constructor(
    private val notificationApi: NotificationApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun getNotifications(username: String, page: Int, size: Int) : ApiResult<List<NotificationDto>> {
        return callApi({ notificationApi.getAllNotifications(username = username, page = page, size = size) }) { response ->
            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody.isNullOrEmpty()) {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
            val jsonObject = gson.fromJson(responseBody, Map::class.java)
            val dataJson = gson.toJson(jsonObject["data"])
            val type = object : TypeToken<List<NotificationDto>>() {}.type
            val notificationDtoList: List<NotificationDto> = gson.fromJson(dataJson, type)
            ApiResult.NetworkResult.Success(notificationDtoList)
        }
    }
}
