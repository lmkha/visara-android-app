package com.example.visara.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.visara.data.local.entity.LocalVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: LocalVideoEntity)

    @Update
    suspend fun updateVideo(video: LocalVideoEntity)

    @Delete
    suspend fun deleteVideo(video: LocalVideoEntity)

    @Query("SELECT * FROM videos WHERE localId = :localId")
    fun getVideoByLocalId(localId: Long) : Flow<LocalVideoEntity?>

    @Query("SELECT * FROM videos WHERE username = :username and statusCode = :statusCode")
    fun getUploadingVideoOfUserByStatusCode(username: String, statusCode: Int) : Flow<List<LocalVideoEntity>>

    @Query("SELECT * FROM videos WHERE username = :username")
    fun getAllLocalVideoByUsername(username: String) : Flow<List<LocalVideoEntity>>
}
