package com.example.visara.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import coil3.Bitmap
import com.example.visara.MainActivity
import com.example.visara.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import androidx.core.graphics.createBitmap

class PushNotificationService : FirebaseMessagingService() {
    /*
    private val inboxRepository : InboxRepository by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            InboxRepositoryEntryPoint::class.java
        ).inboxRepository()
    }
     */

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "your_channel_id"

        val channel = NotificationChannel(channelId, "Chat Notifications", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        thread {
            // Create person
            val bitmap = getBitmapFromURL(avatarUrl)
            val icon = bitmap?.let { IconCompat.createWithBitmap(it) }
            val person = Person.Builder()
                .setName(username)
                .setIcon(icon)
                .build()

            // Create shortcut
            val intent = Intent(this, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val shortcut = ShortcutInfoCompat.Builder(this, username)
                .setLongLived(true)
                .setIntent(intent)
                .setShortLabel(username)
                .setPerson(person)
                .setIcon(icon)
                .build()

            ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)

            // Create MessagingStyle
            val messagingStyle = NotificationCompat.MessagingStyle(person)
            val notificationMessage = NotificationCompat.MessagingStyle.Message(
                content,
                System.currentTimeMillis(),
                person
            )
            messagingStyle.addMessage(notificationMessage)

            // Create notification
            val notification = NotificationCompat.Builder(this, channelId)
                .setStyle(messagingStyle)
                .setShortcutId(username)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()

            // Notify
            notificationManager.notify(123, notification)
        }
    }
    private fun getBitmapFromURL(src: String): Bitmap? {
        fun Bitmap.toCircleBitmap(): Bitmap {
            val size = minOf(width, height)
            val output = createBitmap(size, size)

            val canvas = Canvas(output)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
            val path = Path().apply {
                addOval(rect, Path.Direction.CCW)
            }

            canvas.clipPath(path)
            val left = (size - width) / 2f
            val top = (size - height) / 2f
            canvas.drawBitmap(this, left, top, paint)

            return output
        }
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream).toCircleBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
