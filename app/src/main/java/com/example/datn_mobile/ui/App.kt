package com.example.datn_mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.datn_mobile.ui.screens.HomeScreen
import com.example.datn_mobile.ui.screens.LoginScreen
import com.example.datn_mobile.ui.screens.ProfileScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

@Composable
fun App() {
    val navController = rememberNavController()
//    var authenticated by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Destination.Home.route) {
        composable(Destination.Home.route) {
            HomeScreen {
                navController.navigateToLogin(targetDestination = Destination.Profile.MyProfile())
            }
        }

        loginNavigation { targetDestination->
            navController.navigate(targetDestination)
        }

        navigation<Destination.Profile>(startDestination = Destination.Profile.OtherProfile()) {
            composable<Destination.Profile.MyProfile> {
                ProfileScreen(username = "lmkha", isMyProfile = true)
            }
            composable<Destination.Profile.OtherProfile> {
                ProfileScreen(username = "otherUsername", isMyProfile = false)
            }
        }
    }
}

@Serializable
sealed class Destination(val route: String) {
    @Serializable
    object Home : Destination("home")
    @Serializable
    object Profile : Destination("profile") {
        @Serializable
        data class MyProfile(val username: String = "") : Destination("myProfile")
        @Serializable
        data class OtherProfile(val username: String = "") : Destination("otherProfile")
    }
    @Serializable
    data class Login(val targetDestinationString: String) : Destination("login")

}

val json = Json {
    prettyPrint = true
    classDiscriminator = "type"
    serializersModule = SerializersModule {
        polymorphic(Destination::class, Destination.Home::class, Destination.Home.serializer())
        polymorphic(Destination::class, Destination.Login::class, Destination.Login.serializer())
        polymorphic(Destination::class, Destination.Profile::class, Destination.Profile.serializer())

        polymorphic(Destination.Profile.MyProfile::class, Destination.Profile.MyProfile::class, Destination.Profile.MyProfile.serializer())
        polymorphic(Destination.Profile.OtherProfile::class, Destination.Profile.OtherProfile::class, Destination.Profile.OtherProfile.serializer())
    }
}

fun NavGraphBuilder.loginNavigation(navigateToTargetDestination: (targetDestination: Destination) -> Unit) {
    composable<Destination.Login> {  backStackEntry->
        val targetDestinationString = backStackEntry.toRoute<Destination.Login>().targetDestinationString
        val targetDestination: Destination = json.decodeFromString(targetDestinationString)
        LoginScreen(onAuthenticated = { navigateToTargetDestination(targetDestination) })
    }
}

fun NavController.navigateToLogin(targetDestination: Destination = Destination.Home) {
    val targetDestinationString = json.encodeToString(Destination.serializer(), targetDestination)
    navigate(Destination.Login(targetDestinationString = targetDestinationString))
}





























