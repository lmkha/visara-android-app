package com.example.visara.data.mapper

import com.example.visara.data.local.entity.UserEntity
import com.example.visara.data.model.UserModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserMapper @Inject constructor() {
    fun toModel(entity: UserEntity) : UserModel {
        with(entity) {
            return UserModel(
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
    }

    fun toEntity(model: UserModel) : UserEntity {
        with(model) {
            return  UserEntity(
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
        }
    }
}