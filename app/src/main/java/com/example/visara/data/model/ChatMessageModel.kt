package com.example.visara.data.model


data class ChatMessageModel(
    val content: String = "",
    val senderUsername: String = "",
    val receiverUsername: String? = null,
    val groupId: String? = null,
    val createdTime: String = "",
    val receivedTime: String = "",
    val userReactions: List<UserReaction> = emptyList(),
)

data class UserReaction(
    val username: String = "",
    val reaction: ReactionModel = ReactionModel(),
)

data class ReactionModel(
    val label: String = "",
    val code: String = "",
)
