package com.example.visara.data.remote.datasource

import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.api.UserApi
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
) {
    fun getCurrentUser() : UserModel? {
        val response = userApi.getCurrentUser()
        response?.data?.let { userDto->
            return UserModel(
                id = userDto.id,
                username = userDto.username,
                email = userDto.email,
                phone = userDto.phone,
            )
        }

        return null
    }
}
