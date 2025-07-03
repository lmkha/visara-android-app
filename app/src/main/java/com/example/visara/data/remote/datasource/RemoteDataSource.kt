package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.common.ApiResult
import com.google.gson.Gson

abstract class RemoteDataSource(protected val gson: Gson) {
    fun parseFailureFromResponse(responseBody: String?) : ApiResult.Failure {
        if (responseBody.isNullOrEmpty()) return ApiResult.Failure()
        return gson.fromJson(
            responseBody,
            ApiResult.Failure::class.java
        )
    }
}
