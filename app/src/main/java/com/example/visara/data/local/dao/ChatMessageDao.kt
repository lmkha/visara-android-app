package com.example.visara.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.visara.data.local.entity.ChatMessageWithUserReactions

@Dao
interface ChatMessageDao {
    @Transaction
    @Query("SELECT * FROM messages WHERE id = :id")
    fun getMessageById(id: String) : ChatMessageWithUserReactions
}
