package com.example.visara.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
)
