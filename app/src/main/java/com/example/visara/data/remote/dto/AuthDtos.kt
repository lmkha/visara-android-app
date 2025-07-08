package com.example.visara.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginDto(
    val refreshToken: String = "",
    val accessToken: String = "",
)

@Serializable
data class UsernameAvailabilityDto(
    val isExisted: Boolean = false
)

@Serializable
data class EmailAvailabilityDto(
    val isExisted: Boolean = false,
)

@Serializable
data class RegisterAccountDto(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val phone: String = "",

    @SerialName("profilePic")
    val profileImageLink: String = "",
)

@Serializable
data class RefreshTokenDto(
    val accessToken: String = "",
)
