package com.example.visara.domain

import com.example.visara.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(username: String, password: String) : String? {
        authRepository.login(username, password)
        return authRepository.getToken()
    }
}
