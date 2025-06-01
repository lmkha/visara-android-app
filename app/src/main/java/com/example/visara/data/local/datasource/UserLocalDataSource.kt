package com.example.visara.data.local.datasource

import com.example.visara.data.local.dao.UserDao
import com.example.visara.data.local.shared_preference.UserSessionManager
import com.example.visara.data.model.UserModel
import com.example.visara.data.model.toEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val userSessionManager: UserSessionManager,
) {
    suspend fun getCurrentUser() : UserModel? {
        val currentUserName = userSessionManager.getCurrentUsername() ?: return null
        val currentUser = userDao
            .getUserByUsernameDistinctUntilChanged(currentUserName)
            .first()
            ?.toUserModel()
        return currentUser
    }

    suspend fun saveUser(userModel : UserModel) {
        val userEntity = userModel.toEntity()
        userDao.insertUser(userEntity)
    }
}
