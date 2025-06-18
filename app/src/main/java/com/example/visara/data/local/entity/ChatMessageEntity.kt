package com.example.visara.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.visara.data.model.ChatMessageModel
import com.example.visara.data.model.ReactionModel
import com.example.visara.data.model.UserReaction

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val content: String = "",
    val senderUsername: String = "",
    val groupId: String? = null,
    val receiverUsername: String? = null,
    val createdTime: String = "",
    val receivedTime: String = "",
) {
    fun toChatMessageModel(userReactions: List<UserReaction> = emptyList()) = ChatMessageModel(
        content = this.content,
        senderUsername = this.senderUsername,
        groupId = this.groupId,
        receiverUsername = this.receiverUsername,
        createdTime = this.createdTime,
        receivedTime = this.receivedTime,
        userReactions = userReactions,
    )
}

@Entity(tableName = "user_reactions")
data class UserReactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val messageId: String,
    val username: String,
    val reactionCode: String,
    val reactionLabel: String,
) {
    fun toUserReaction() = UserReaction(
        username = this.username,
        reaction = ReactionModel(
            label = this.reactionLabel,
            code = this.reactionCode,
        )
    )
}

data class ChatMessageWithUserReactions(
    @Embedded val message: ChatMessageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId"
    )
    val userReactions: List<UserReactionEntity>
) {
    fun toChatMessageModel(): ChatMessageModel {
        val userReactions = this.userReactions.map { it.toUserReaction() }
        val chatMessageModel = this.message.toChatMessageModel(userReactions)
        return chatMessageModel
    }
}
