package com.example.visara.data.model

data class UserModel(
    val username: String = "",
    val id: Long = 0L,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val networkAvatarUrl: String = "",
    val localAvatarPath: String = "",
    val bio: String = "",
    val isPrivate: Boolean = false,
    val dob: String = "",
    val isVerified: Boolean = false,
    val followerCount: Long = 0L,
    val followingCount: Long = 0L,
)
