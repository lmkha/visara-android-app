package com.example.visara.domain

import com.example.visara.data.repository.AuthRepository
import com.example.visara.data.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(username: String, password: String) : Result<Unit> {
        val loginResult = authRepository.login(username, password)
        if (loginResult.isSuccess) {
            userRepository.syncCurrentUser()
            return Result.success(Unit)
        } else {
            return Result.failure(Throwable(loginResult.exceptionOrNull()?.message))
        }
    }
}
