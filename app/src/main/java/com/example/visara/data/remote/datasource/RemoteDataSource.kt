package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response

abstract class RemoteDataSource(protected val gson: Gson) {
    suspend fun <T> callApi(
        request: suspend () -> Response,
        handleResponse: (Response) -> ApiResult.NetworkResult<T>
    ): ApiResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = request()
                return@withContext handleResponse(response)
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }

    fun extractFailureFromResponseBody(responseBody: String?) : ApiResult.NetworkResult.Failure {
        if (responseBody.isNullOrEmpty()) return ApiResult.NetworkResult.Failure()
        return gson.fromJson(
            responseBody,
            ApiResult.NetworkResult.Failure::class.java
        )
    }
}
