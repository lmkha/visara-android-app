package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Response

abstract class RemoteDataSource(protected val json: Json) {
    protected suspend fun <T> callApi(
        request: suspend () -> Response,
        handleResponse: (Response) -> ApiResult<T>
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

    protected fun extractFailureFromResponseBody(responseBody: String?) : ApiResult.Failure {
        if (responseBody.isNullOrEmpty()) return ApiResult.Failure()
        return json.decodeFromString<ApiResult.Failure>(responseBody)
    }
}
