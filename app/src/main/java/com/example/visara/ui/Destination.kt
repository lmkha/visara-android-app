package com.example.visara.ui

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination {
    @Serializable
    data object Main : Destination() {
        @Serializable
        data object Home : Destination()
        @Serializable
        data object FollowingFeed : Destination()
        @Serializable
        data class AddNewVideo(
            val isPostDraft: Boolean = false,
            val localDraftVideoId: Long? = null,
        ) : Destination()
        @Serializable
        data object Inbox : Destination() {
            @Serializable
            data object InboxList : Destination()
            @Serializable
            data object NewFollowersInbox : Destination()
            @Serializable
            data object ActivityInbox : Destination()
            @Serializable
            data object SystemNotificationInbox : Destination()
            @Serializable
            data object Studio : Destination()
            @Serializable
            data class ChatInbox(val username: String) : Destination()
        }
        @Serializable
        data class Profile(
            @SerialName("username")
            val username: String?,
            val shouldNavigateToMyProfile: Boolean = false,
        ) : Destination()
    }
    @Serializable
    data object EditProfile: Destination()
    @Serializable
    data class Follow(val startedTabIndex: Int = 0): Destination()
    @Serializable
    data object Settings: Destination()
    @Serializable
    data class Search(
        val type: String = "",
        val pattern: String = "",
    ): Destination()
    @Serializable
    data object Login : Destination()
    @Serializable
    data class Studio(val selectedTagIndex: Int = 0) : Destination()
    @Serializable
    data class EditVideo(val videoJson: String) : Destination()
    @Serializable
    data object Test : Destination()
    @Serializable
    data object QRCode : Destination() {
        @Serializable
        data object MyQRCode : Destination()
        @Serializable
        data object ScanQRCode: Destination()
        @Serializable
        data class UnRecognizedQRCodeScanned(val data: String) : Destination()
    }
}
