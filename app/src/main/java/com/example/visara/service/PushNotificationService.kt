package com.example.visara.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import coil3.Bitmap
import com.example.visara.R
import com.example.visara.data.repository.InboxRepository
import com.example.visara.di.InboxRepositoryEntryPoint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.EntryPointAccessors
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class PushNotificationService : FirebaseMessagingService() {
    private val inboxRepository : InboxRepository by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            InboxRepositoryEntryPoint::class.java
        ).inboxRepository()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val tag = "CHECK_VAR"
        // (developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(tag, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(tag, "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(tag, "Message Notification Body: ${it.body}")
        }

        val data = remoteMessage.data
        val content = data["content"] ?: "No content"
        val username = data["username"] ?: "Unknown"
        val avatarUrl = data["avatar"] ?: ""

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "your_channel_id"

        val channel = NotificationChannel(channelId, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

//        thread {
//            val bitmap = getBitmapFromURL(avatarUrl)
//
//            val notification = NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.app_logo)
//                .setLargeIcon(bitmap)
//                .setContentTitle(username)
//                .setContentText(content)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .build()
//
//            notificationManager.notify(123, notification)
//        }
        thread {
            val bitmap = getBitmapFromURL(avatarUrl)

            val me = Person.Builder()
                .setName("Bạn")
                .setKey("me")
                .setImportant(true)
                .build()

            val sender = Person.Builder()
                .setName(username)
                .setIcon(bitmap?.let { IconCompat.createWithBitmap(it) })
                .build()

            val messagingStyle = NotificationCompat.MessagingStyle(me)
                .addMessage(content, System.currentTimeMillis(), sender)

            val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.app_logo)
                .setStyle(messagingStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(123, notification)
        }
    }
    private fun getBitmapFromURL(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
