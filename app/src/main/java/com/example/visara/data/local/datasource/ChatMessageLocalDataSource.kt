package com.example.visara.data.local.datasource

import com.example.visara.data.local.dao.ChatMessageDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatMessageLocalDataSource @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
) {

}
