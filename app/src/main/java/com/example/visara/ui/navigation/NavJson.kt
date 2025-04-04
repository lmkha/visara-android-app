package com.example.visara.ui.navigation

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val navJson: Json = Json {
    prettyPrint = true
    classDiscriminator = "type"
    serializersModule = SerializersModule {
        polymorphic(Destination::class) {
            subclass(Destination.Main::class, Destination.Main.serializer())
                subclass(Destination.Main.Home::class, Destination.Main.Home.serializer())
                subclass(Destination.Main.Following::class, Destination.Main.Following.serializer())
                subclass(Destination.Main.AddNewVideo::class, Destination.Main.AddNewVideo.serializer())
                subclass(Destination.Main.Mail::class, Destination.Main.Mail.serializer())
                subclass(Destination.Main.Profile::class, Destination.Main.Profile.serializer())

            subclass(Destination.Settings::class, Destination.Settings.serializer())
                subclass(Destination.Settings.Account::class, Destination.Settings.Account.serializer())
                subclass(Destination.Settings.Privacy::class, Destination.Settings.Privacy.serializer())
                subclass(Destination.Settings.Security::class, Destination.Settings.Security.serializer())

            subclass(Destination.Search::class, Destination.Search.serializer())
            subclass(Destination.Login::class, Destination.Login.serializer())
            subclass(Destination.SignUp::class, Destination.SignUp.serializer())
        }
//        polymorphic(NavDestination::class, NavDestination.Main::class, NavDestination.Main.serializer())
//        polymorphic(NavDestination::class, NavDestination.Settings::class, NavDestination.Settings.serializer())
//        polymorphic(NavDestination::class, NavDestination.Search::class, NavDestination.Search.serializer())
//        polymorphic(NavDestination::class, NavDestination.Login::class, NavDestination.Login.serializer())
//        polymorphic(NavDestination::class, NavDestination.SignUp::class, NavDestination.SignUp.serializer())
//
//        polymorphic(NavDestination.Main.Home::class, NavDestination.Main.Home::class, NavDestination.Main.Home.serializer())
//        polymorphic(NavDestination.Main.Following::class, NavDestination.Main.Following::class, NavDestination.Main.Following.serializer())
//        polymorphic(NavDestination.Main.AddNewVideo::class, NavDestination.Main.AddNewVideo::class, NavDestination.Main.AddNewVideo.serializer())
//        polymorphic(NavDestination.Main.Mail::class, NavDestination.Main.Mail::class, NavDestination.Main.Mail.serializer())
//        polymorphic(NavDestination.Main.Profile::class, NavDestination.Main.Profile::class, NavDestination.Main.Profile.serializer())
    }
}
