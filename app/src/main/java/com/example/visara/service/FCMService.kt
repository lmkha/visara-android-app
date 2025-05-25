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
import androidx.core.graphics.createBitmap
import com.example.visara.data.repository.InboxRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject lateinit var inboxRepository: InboxRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("CHECK_VAR", "Message data payload: ${remoteMessage.data}")
        }

        val data = remoteMessage.data
        val content = data["content"] ?: "No content"
        val username = data["username"] ?: "Unknown"
        val avatarUrl = data["avatar"] ?: ""

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "chat_message_channel"

        val channel = NotificationChannel(channelId, "Chat notification", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        CoroutineScope(Dispatchers.IO).launch {
            // Create person
            val bitmap = getBitmapFromURL(avatarUrl)
            val icon = bitmap?.let { IconCompat.createWithBitmap(it) }
            val person = Person.Builder()
                .setName(username)
                .setIcon(icon)
                .build()

            // Create shortcut
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val shortcut = ShortcutInfoCompat.Builder(applicationContext, username)
                .setLongLived(true)
                .setIntent(intent)
                .setShortLabel(username)
                .setPerson(person)
                .setIcon(icon)
                .build()

            ShortcutManagerCompat.pushDynamicShortcut(applicationContext, shortcut)

            // Create MessagingStyle
            val messagingStyle = NotificationCompat.MessagingStyle(person)
            val notificationMessage = NotificationCompat.MessagingStyle.Message(
                content,
                System.currentTimeMillis(),
                person
            )
            messagingStyle.addMessage(notificationMessage)

            // Create notification
            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setStyle(messagingStyle)
                .setShortcutId(username)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
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
