package com.example.visara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.visara.data.model.UserModel

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val id: Long,
    val fullName: String,
    val email: String,
    val phone: String,
    val networkAvatarUrl: String,
    val localAvatarPath: String,
    val bio: String,
    val isPrivate: Boolean,
    val dob: String,
    val isVerified: Boolean,
    val followerCount: Long,
    val followingCount: Long,
) {
    fun toUserModel(): UserModel = UserModel(
        id = id,
        username = username,
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
}
