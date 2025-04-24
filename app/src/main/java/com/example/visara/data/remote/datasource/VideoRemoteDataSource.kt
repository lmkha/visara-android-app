package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.ApiError
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.api.VideoApi
import com.example.visara.data.remote.dto.VideoDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoRemoteDataSource @Inject constructor(
    private val videoApi: VideoApi,
    private val gson: Gson,
) {
    suspend fun getRandomVideos(numOfVideos: Int = 10) : ApiResult<List<VideoDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = videoApi.getRandomVideos(numOfVideos)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<VideoDto>>() {}.type
                    val videoList: List<VideoDto> = gson.fromJson(dataJson, type)

                    ApiResult.Success(videoList)

                } else ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }
}
