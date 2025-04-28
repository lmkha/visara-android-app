package com.example.visara.data.remote.datasource

import com.example.visara.data.remote.api.CommentApi
import com.example.visara.data.remote.common.ApiError
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.dto.CommentDto
import com.example.visara.data.remote.dto.LikeCommentDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CommentRemoteDataSource @Inject constructor(
    private val commentApi: CommentApi,
    private val gson: Gson,
) {
    suspend fun addComment(videoId: String, replyTo: String, content: String) : ApiResult<CommentDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.addComment(videoId, replyTo, content)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val commentDto = gson.fromJson(dataJson, CommentDto::class.java)
                    ApiResult.Success(commentDto)
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

    suspend fun getCommentById(commentId: String) : ApiResult<CommentDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.getCommentById(commentId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val commentDto = gson.fromJson(dataJson, CommentDto::class.java)
                    ApiResult.Success(commentDto)
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

    suspend fun getAllParentCommentsByVideoId(
        videoId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : ApiResult<List<CommentDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.getAllParentCommentsByVideoId(videoId, order, page, size)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<CommentDto>>() {}.type
                    val commentDtoList: List<CommentDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(commentDtoList)
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

    suspend fun getAllChildrenComment(
        parentId: String,
        order: String,
        page: Int = 0,
        size: Int = 10,
    ) : ApiResult<List<CommentDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.getAllChildrenComment(parentId, order, page, size)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val type = object : TypeToken<List<CommentDto>>() {}.type
                    val commentDtoList: List<CommentDto> = gson.fromJson(dataJson, type)
                    ApiResult.Success(commentDtoList)
                } else {
                    ApiResult.Failure(
                        ApiError(
                            code = response.code,
                            errorCode = response.code.toString(),
                            message = response.message,
                            rawBody = responseBody,
                        )
                    )
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun likeComment(commentId: String) : ApiResult<LikeCommentDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.likeComment(commentId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val likeCommentDto = gson.fromJson(dataJson, LikeCommentDto::class.java)
                    ApiResult.Success(likeCommentDto)
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

    suspend fun unlikeComment(commentId: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.likeComment(commentId)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    ApiResult.Success(Unit)
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

    suspend fun checkCommentLike(commentId: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.checkCommentLike(commentId)
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val message = jsonObject["message"]
                    if (message is String && message.isNotBlank() && message == "Liked") {
                        return@withContext ApiResult.Success(Unit)
                    }
                }

                return@withContext ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }

    suspend fun deleteComment(commentId: String) : ApiResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.deleteComment(commentId)
                val responseBody = response.body?.string()

                if (response.isSuccessful) {
                    val jsonObject =  gson.fromJson(responseBody, Map::class.java)
                    val success = jsonObject["success"]
                    if (success is Boolean && success == true) {
                        return@withContext ApiResult.Success(Unit)
                    }
                }

                return@withContext ApiResult.Failure(
                    ApiError(
                        code = response.code,
                        errorCode = response.code.toString(),
                        message = response.message,
                        rawBody = responseBody
                    )
                )
            } catch (e: Exception) {
                return@withContext ApiResult.Error(e)
            }
        }
    }

    suspend fun updateComment(commentId: String, content: String) : ApiResult<CommentDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response = commentApi.updateComment(commentId, content)
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val jsonObject = gson.fromJson(responseBody, Map::class.java)
                    val dataJson = gson.toJson(jsonObject["data"])
                    val commentDto = gson.fromJson(dataJson, CommentDto::class.java)
                    ApiResult.Success(commentDto)
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
