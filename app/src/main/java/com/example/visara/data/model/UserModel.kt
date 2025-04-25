package com.example.visara.data.model

import com.example.visara.data.local.entity.UserEntity

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
) {
    override fun toString(): String {
        return "[UserModel] id = $id, username = $username, email = $email, phone = $phone"
    }
}
fun UserModel.toEntity(): UserEntity = UserEntity(
    username = username,
    id = id,
    fullName = fullName,
    email = email,
    phone = phone,
    networkAvatarUrl = networkAvatarUrl,
    localAvatarPath = localAvatarPath,
    bio = bio,
    isPrivate = isPrivate,
    dob = dob,
    isVerified = isVerified,
    followerCount = followerCount,
    followingCount = followingCount,
)
