package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.NotificationApi
import com.example.visara.data.remote.common.ApiError
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.NotificationDto
import com.example.visara.di.gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRemoteDataSource @Inject constructor(
    private val notificationApi: NotificationApi,
) {
    suspend fun getNotifications(username: String, page: Int, size: Int) : ApiResult<List<NotificationDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = notificationApi.getAllNotifications(username = username, page = page, size = size)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<NotificationDto>>() {}.type
                    val notificationDtoList: List<NotificationDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(notificationDtoList)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
