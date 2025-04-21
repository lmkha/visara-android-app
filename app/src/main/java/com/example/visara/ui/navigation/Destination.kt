package com.example.visara.ui.navigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val name: String) {
    val route: String get() = this::class.qualifiedName ?: this.toString()

    @Serializable data class Main(val targetDestinationString: String? = null) : Destination("Main") {
        @Serializable object Home : Destination("Main.Home")
        @Serializable object Following : Destination("Main.Following")
        @Serializable object AddNewVideo : Destination("Main.AddNewVideo")
        @Serializable object Inbox : Destination("Main.Inbox")
        @Serializable data class Profile(val username: String? = null) : Destination("Main.Profile")
    }

    @Serializable object Settings: Destination("Settings")
    @Serializable object Search: Destination("Search")
    @Serializable object Login : Destination("Login")
    @Serializable object SignUp : Destination("SignUp")
    @Serializable object Studio : Destination("Studio")
    @Serializable object Test : Destination("Test")
}
