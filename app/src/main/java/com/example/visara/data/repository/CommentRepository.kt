package com.example.visara.data.repository

import com.example.visara.data.remote.datasource.CommentRemoteDataSource
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentRemoteDataSource: CommentRemoteDataSource,
) {
}
