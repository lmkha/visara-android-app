package com.example.visara.data.remote.dto

import com.example.visara.data.model.UserModel

data class UserDto(
    val createdAt: String = "",
    val updatedAt: String = "",
    val id: Long = 0L,
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val profilePic: String = "",
    val bio: String = "",
    val isPrivate: Boolean = false,
    val dateOfBirth: String = "",
    val isVerified: Boolean = false,
    val followerCount: Long = 0L,
    val followingCount: Long = 0L,
    val enabled: Boolean = false,
    val accountNonExpired: Boolean = false,
    val credentialsNonExpired: Boolean = false,
    val accountNonLocked: Boolean = false,
) {
    fun toUserModel(): UserModel {
        return UserModel(
            username = this.username,
            id = this.id,
            fullName = this.fullName,
            email = this.email,
            phone = this.phone,
            networkAvatarUrl = this.profilePic,
            localAvatarPath = "",
            bio = this.bio,
            isPrivate = this.isPrivate,
            dob = this.dateOfBirth,
            isVerified = this.isVerified,
            followerCount = this.followerCount,
            followingCount = this.followingCount
        )
    }
}

data class FollowingUserDto(
    val id: Long = 0L,
    val username: String = "",
    val fullName: String = "",
    val profilePic: String = "",
) {
    fun toUserModel(): UserModel {
        return UserModel(
            username = this.username,
            id = this.id,
            fullName = this.fullName,
            networkAvatarUrl = this.profilePic,
            localAvatarPath = "",
        )
    }
}

data class FollowerUserDto(
    val id: Long = 0L,
    val username: String = "",
    val fullName: String = "",
    val profilePic: String = "",
    val isFollowing: Boolean = false,
) {

    fun toUserModel(): UserModel {
        return UserModel(
            username = this.username,
            id = this.id,
            fullName = this.fullName,
            networkAvatarUrl = this.profilePic,
            localAvatarPath = "",
        )
    }
}
