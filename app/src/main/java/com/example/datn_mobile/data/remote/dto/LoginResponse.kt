package com.example.datn_mobile.data.remote.dto

data class LoginResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: String = "",
)
