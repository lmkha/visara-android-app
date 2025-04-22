package com.example.visara.data.remote.datasource

import com.example.visara.data.model.UserModel
import com.example.visara.data.remote.ApiResult
import com.example.visara.data.remote.api.UserApi
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val userApi: UserApi,
) {
    suspend fun getCurrentUser() : UserModel? {
        val apiResult = userApi.getCurrentUser()
        if (apiResult is ApiResult.Success) {
            apiResult.data.let { userDto->
                return UserModel(
                    id = userDto.id,
                    username = userDto.username,
                    email = userDto.email,
                    phone = userDto.phone,
                )
            }
        }
        return null
    }
}
