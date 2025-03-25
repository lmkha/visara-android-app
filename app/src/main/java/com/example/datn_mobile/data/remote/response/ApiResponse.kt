package com.example.datn_mobile.data.remote.response

data class ApiResponse<T>(
    val success: Boolean = false,
    val message: String = "",
    val data: T? = null
)
