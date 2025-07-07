package com.example.visara.data.remote.dto

import com.example.visara.data.model.FollowUserModel
import com.example.visara.data.model.UserModel
import kotlinx.serialization.Serializable

@Serializable
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

@Serializable
data class FollowingUserDto(
    val id: Long = 0L,
    val username: String = "",
    val fullName: String = "",
    val profilePic: String = "",
) {
    fun toFollowUserModel() : FollowUserModel {
        return FollowUserModel(
            user = UserModel(
                username = this.username,
                id = this.id,
                fullName = this.fullName,
                networkAvatarUrl = this.profilePic,
            ),
            isFollowing = true
        )
    }
}

@Serializable
data class FollowerUserDto(
    val id: Long = 0L,
    val username: String = "",
    val fullName: String = "",
    val profilePic: String = "",
    val isFollowing: Boolean = false,
) {
    fun toFollowUserModel() : FollowUserModel {
        return FollowUserModel(
            user = UserModel(
                username = this.username,
                id = this.id,
                fullName = this.fullName,
                networkAvatarUrl = this.profilePic,
            ),
            isFollowing = this.isFollowing,
        )
    }
}
