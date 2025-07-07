package com.example.visara.domain

import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke() {
        authRepository.logout()
        userRepository.refreshCurrentUser()
    }
}
