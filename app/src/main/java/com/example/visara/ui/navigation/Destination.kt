package com.example.visara.ui.navigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val name: String) {
    val route: String get() = this::class.qualifiedName ?: this.toString()

    @Serializable object Main : Destination("Main") {
        @Serializable object Home : Destination("Main.Home")
        @Serializable object FollowingFeed : Destination("Main.FollowingFeed")
        @Serializable data class AddNewVideo(
            val isPostDraft: Boolean = false,
            val localDraftVideoId: Long? = null,
        ) : Destination("Main.AddNewVideo")
        @Serializable object Inbox : Destination("Main.Inbox") {
            @Serializable object InboxList : Destination("Main.Inbox.InboxList")
            @Serializable object NewFollowersInbox : Destination("Main.Inbox.NewFollowers")
            @Serializable object ActivityInbox : Destination("Main.Inbox.Activity")
            @Serializable object SystemNotificationInbox : Destination("Main.Inbox.SystemNotification")
            @Serializable object Studio : Destination("Main.Inbox.Studio")
            @Serializable data class ChatInbox(val username: String) : Destination("Main.Inbox.Chat")
        }
        @Serializable data class Profile(
            val username: String? = null,
            val shouldNavigateToMyProfile: Boolean = false,
        ) : Destination("Main.Profile")
    }
    @Serializable data class Follow(val startedTabIndex: Int = 0): Destination("Follow")
    @Serializable object Settings: Destination("Settings")
    @Serializable data class Search(
        val type: String = "",
        val pattern: String = "",
    ): Destination("Search")
    @Serializable object Login : Destination("Login")
    //    @Serializable object SignUp : Destination("SignUp")
    @Serializable data class Studio(val selectedTagIndex: Int = 0) : Destination("Studio")
    @Serializable data class EditVideo(val videoJson: String) : Destination("EditVideo")
    @Serializable object Test : Destination("Test")
}
