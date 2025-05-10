package com.example.visara.data.model

data class FollowUserModel(
    val user: UserModel,
    val isFollowing: Boolean = false,
)
