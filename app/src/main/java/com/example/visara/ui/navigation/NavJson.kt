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
                subclass(Destination.Main.Inbox::class, Destination.Main.Inbox.serializer())
                subclass(Destination.Main.Profile::class, Destination.Main.Profile.serializer())

            subclass(Destination.Settings::class, Destination.Settings.serializer())

            subclass(Destination.Search::class, Destination.Search.serializer())
            subclass(Destination.Login::class, Destination.Login.serializer())
            subclass(Destination.SignUp::class, Destination.SignUp.serializer())
        }
    }
}
