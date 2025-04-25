package com.example.visara.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.visara.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): Flow<UserEntity>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    fun getUserByUsernameDistinctUntilChanged(username: String): Flow<UserEntity> {
        return getUserByUsername(username).distinctUntilChanged()
    }
}
