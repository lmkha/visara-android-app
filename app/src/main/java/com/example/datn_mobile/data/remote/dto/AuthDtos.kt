package com.example.datn_mobile.data.remote.dto

import com.google.gson.annotations.SerializedName

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

    @SerializedName("profilePic")
    val profileImageLink: String = "",
)
