package com.example.datn_mobile.domain

import com.example.datn_mobile.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(username: String, password: String) : String? {
        authRepository.login(username, password)
        return authRepository.getToken()
    }
}
