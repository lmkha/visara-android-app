package com.example.visara.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginDto(
    val refreshToken: String = "",
    val accessToken: String = "",
)

data class UsernameAvailabilityDto(
    val isExisted: Boolean = false
)

data class EmailAvailabilityDto(
    val isExisted: Boolean = false,
)

data class RegisterAccountDto(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val phone: String = "",

    @field:SerializedName("profilePic")
    val profileImageLink: String = "",
)

data class RefreshTokenDto(
    val accessToken: String = "",
)
