package com.example.visara.data.repository

import com.example.visara.data.model.UserModel
import com.example.visara.data.model.VideoModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val videoRepository: VideoRepository,
    private val userRepository: UserRepository,
) {
    suspend fun searchVideo(type: String = "title", pattern: String, count: Long = 20) : List<VideoModel> {
        return videoRepository.searchVideo(type, pattern, count)
    }

    suspend fun searchUser(pattern: String) : List<UserModel> {
        return userRepository.searchUser(pattern)
    }
}
