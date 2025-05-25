package com.example.visara.data.repository

import com.example.visara.data.model.CommentModel
import com.example.visara.data.remote.common.ApiResult
import com.example.visara.data.remote.datasource.CommentRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val authRepository: AuthRepository,
) {
    suspend fun getAllParentCommentsByVideoId(
        videoId: String,
        order: String = "newest",
        page: Int = 0,
        size: Int = 10,
    ) : List<CommentModel> {
        val needAuthenticate = authRepository.isAuthenticated.value
        val apiResult = commentRemoteDataSource.getAllParentCommentsByVideoId(
            needAuthenticate = needAuthenticate,
            videoId = videoId,
            order = order,
            page = page,
            size = size
        )

        if (apiResult is ApiResult.Success) {
            val commentModelList = apiResult.data.map { it.toCommentModel() }
            return commentModelList
        }

        return emptyList()
    }

    suspend fun getAllChildrenCommentByParentId(
        parentCommentId: String,
        order: String = "oldest",
        page: Int = 0,
        size: Int = 10,
    ) : List<CommentModel> {
        val needAuthenticate = authRepository.isAuthenticated.value
        val apiResult = commentRemoteDataSource.getAllChildrenComment(
            needAuthenticate = needAuthenticate,
            parentId = parentCommentId,
            order = order,
            page = page,
            size = size,
        )

        if (apiResult is ApiResult.Success) {
            val commentModelList = apiResult.data.map { it.toCommentModel() }
            return commentModelList
        }

        return emptyList()
    }

    suspend fun likeComment(commentId: String) : Boolean {
        val apiResult = commentRemoteDataSource.likeComment(commentId)
        return apiResult is ApiResult.Success
    }

    suspend fun unlikeComment(commentId: String) : Boolean {
        val apiResult = commentRemoteDataSource.unlikeComment(commentId)
        return apiResult is ApiResult.Success
    }

    suspend fun addComment(videoId: String, replyTo: String?, content: String) : CommentModel? {
        val apiResult = commentRemoteDataSource.addComment(videoId, replyTo, content)
        var newComment: CommentModel? = null

        if (apiResult is ApiResult.Success) {
            newComment = apiResult.data.toCommentModel()
        }

        return newComment
    }
}
