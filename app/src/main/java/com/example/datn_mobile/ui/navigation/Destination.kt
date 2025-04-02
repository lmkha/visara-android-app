package com.example.datn_mobile.ui.navigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val route: String) {
    override fun toString(): String {
        return this::class.qualifiedName ?: super.toString()
    }
    @Serializable data class Main(val targetDestinationString: String? = null) : Destination("Main") {
        @Serializable object Home : Destination("Main.Home")
        @Serializable object Following : Destination("Main.Following")
        @Serializable object AddNewVideo : Destination("Main.AddNewVideo")
        @Serializable object Mail : Destination("Main.Mail")
        @Serializable object Profile : Destination("Main.Profile")
    }

    @Serializable object Settings: Destination("Settings") {
        @Serializable object Account : Destination("Settings.Account")
        @Serializable object Privacy : Destination("Settings.Privacy")
        @Serializable object Security : Destination("Settings.Security")
    }
    @Serializable object Search: Destination("Search")
    @Serializable object Login : Destination("Login")
    @Serializable object SignUp : Destination("SignUp")
}
