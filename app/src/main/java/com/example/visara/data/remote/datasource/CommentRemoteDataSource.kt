package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.CommentApi
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.CommentDto
import com.example.visara.data.remote.dto.LikeCommentDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRemoteDataSource @Inject constructor(
    private val commentApi: CommentApi,
    gson: Gson,
) : RemoteDataSource(gson) {
    suspend fun addComment(videoId: String, replyTo: String?, content: String) : ApiResult<CommentDto> {
        return callApi({ commentApi.addComment(videoId, replyTo, content) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val commentDto = gson.fromJson(dataJson, CommentDto::class.java)
                ApiResult.NetworkResult.Success(commentDto)
            } else {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getCommentById(commentId: String) : ApiResult<CommentDto> {
        return callApi({ commentApi.getCommentById(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val commentDto = gson.fromJson(dataJson, CommentDto::class.java)
                ApiResult.NetworkResult.Success(commentDto)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllParentCommentsByVideoId(
        needAuthenticate: Boolean = false,
        videoId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : ApiResult<List<CommentDto>> {
        return callApi({ commentApi.getAllParentCommentsByVideoId(needAuthenticate, videoId, order, page, size) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<CommentDto>>() {}.type
                val commentDtoList: List<CommentDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(commentDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getAllChildrenComment(
        needAuthenticate: Boolean = false,
        parentId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : ApiResult<List<CommentDto>> {
        return callApi({ commentApi.getAllChildrenComment(needAuthenticate, parentId, order, page, size) }) { response ->
            val response = commentApi.getAllChildrenComment(needAuthenticate, parentId, order, page, size)
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val type = object : TypeToken<List<CommentDto>>() {}.type
                val commentDtoList: List<CommentDto> = gson.fromJson(dataJson, type)
                ApiResult.NetworkResult.Success(commentDtoList)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun likeComment(commentId: String) : ApiResult<LikeCommentDto> {
        return callApi({ commentApi.likeComment(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val likeCommentDto = gson.fromJson(dataJson, LikeCommentDto::class.java)
                ApiResult.NetworkResult.Success(likeCommentDto)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun unlikeComment(commentId: String) : ApiResult<Unit> {
        return callApi({ commentApi.unlikeComment(commentId) }) { response ->
            val responseBody = response.body?.string()
            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                ApiResult.NetworkResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun checkCommentLike(commentId: String): ApiResult<Unit> {
        return callApi({ commentApi.checkCommentLike(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val message = jsonObject["message"]
                if (message is String && message.isNotBlank() && message == "Liked") {
                    return@callApi ApiResult.NetworkResult.Success(Unit)
                }
            }

            return@callApi extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun deleteComment(commentId: String) : ApiResult<Unit> {
        return callApi({ commentApi.deleteComment(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                val success = jsonObject["success"]
                if (success is Boolean && success) {
                    return@callApi ApiResult.NetworkResult.Success(Unit)
                }
            }

            return@callApi extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun updateComment(commentId: String, content: String) : ApiResult<CommentDto> {
        return callApi({ commentApi.updateComment(commentId, content) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val jsonObject = gson.fromJson(responseBody, Map::class.java)
                val dataJson = gson.toJson(jsonObject["data"])
                val commentDto = gson.fromJson(dataJson, CommentDto::class.java)
                ApiResult.NetworkResult.Success(commentDto)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }
}
