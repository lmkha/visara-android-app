package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.CommentApi
import com.example.visara.data.remote.common.ApiResponse
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.CommentDto
import com.example.visara.data.remote.dto.LikeCommentDto
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRemoteDataSource @Inject constructor(
    private val commentApi: CommentApi,
    json: Json,
) : RemoteDataSource(json) {
    suspend fun addComment(videoId: String, replyTo: String?, content: String) : ApiResult<CommentDto> {
        return callApi({ commentApi.addComment(videoId, replyTo, content) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<CommentDto>>(responseBody).toApiResult()
            } else {
                return@callApi extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun getCommentById(commentId: String) : ApiResult<CommentDto> {
        return callApi({ commentApi.getCommentById(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<CommentDto>>(responseBody).toApiResult()
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
                json.decodeFromString<ApiResponse<List<CommentDto>>>(responseBody).toApiResult()
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
                json.decodeFromString<ApiResponse<List<CommentDto>>>(responseBody).toApiResult()
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun likeComment(commentId: String) : ApiResult<LikeCommentDto> {
        return callApi({ commentApi.likeComment(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResult.Success<LikeCommentDto>>(responseBody)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun unlikeComment(commentId: String) : ApiResult<Unit> {
        return callApi({ commentApi.unlikeComment(commentId) }) { response ->
            val responseBody = response.body?.string()
            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                ApiResult.Success(Unit)
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }

    suspend fun checkCommentLike(commentId: String): ApiResult<Unit> {
        return callApi({ commentApi.checkCommentLike(commentId) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()
                json.decodeFromString<ApiResponse<Nothing>>(responseBody).let {
                    if (it.message == "Liked") {
                        ApiResult.Success(Unit)
                    } else {
                        ApiResult.Failure()
                    }
                }
            }

            return@callApi extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun deleteComment(commentId: String) : ApiResult<Unit> {
        return callApi({ commentApi.deleteComment(commentId) }) { response ->
            val responseBody = response.body?.string() ?: return@callApi ApiResult.Failure()

            if (response.isSuccessful) {
                json.decodeFromString<ApiResponse<Unit>>(responseBody)
            }

            return@callApi extractFailureFromResponseBody(responseBody)
        }
    }

    suspend fun updateComment(commentId: String, content: String) : ApiResult<CommentDto> {
        return callApi({ commentApi.updateComment(commentId, content) }) { response ->
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                json.decodeFromString<ApiResponse<CommentDto>>(responseBody).toApiResult()
            } else {
                extractFailureFromResponseBody(responseBody)
            }
        }
    }
}
