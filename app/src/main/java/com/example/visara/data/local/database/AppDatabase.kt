package com.example.visara.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.visara.data.local.dao.NotificationDao
import com.example.visara.data.local.dao.UserDao
import com.example.visara.data.local.dao.VideoDao
import com.example.visara.data.local.entity.LocalVideoEntity
import com.example.visara.data.local.entity.NotificationEntity
import com.example.visara.data.local.entity.UserEntity

@Database(
    version = 1,
    entities = [
        UserEntity::class,
        LocalVideoEntity::class,
        NotificationEntity::class,
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao

    abstract fun videoDao() : VideoDao

    abstract fun notificationDao() : NotificationDao
}
