package com.example.visara.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.visara.data.local.dao.UserDao
import com.example.visara.data.local.entity.UserEntity

@Database(
    version = 1,
    entities = [
        UserEntity::class,
    ],
//    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao

}
