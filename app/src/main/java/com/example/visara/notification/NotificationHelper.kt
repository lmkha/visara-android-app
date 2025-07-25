package com.example.visara.notification

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import coil3.Bitmap
import com.example.visara.MainActivity
import com.example.visara.R
import com.example.visara.data.model.CommentLikeNotificationData
import com.example.visara.data.model.CommentOnVideoNotificationData
import com.example.visara.data.model.NewChatMessageNotificationData
import com.example.visara.data.model.NotificationModel
import com.example.visara.data.model.VideoLikeNotificationData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
) {
    fun createVideoProcessedNotificationBuilder(title: String, thumbnailUrl: String) : NotificationCompat.Builder {
        val thumbnailBitmap = getCircleBitmapFromURL(thumbnailUrl)
        return NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle("Your video was processed successfully.")
            .setContentText(title)
            .setSmallIcon(R.drawable.app_logo)
            .setLargeIcon(thumbnailBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    fun createMessageNotification(messageDto: NewChatMessageNotificationData) : Notification {
        // Create person
        val bitmap = getCircleBitmapFromURL(messageDto.senderAvatar)
        val icon = bitmap?.let { IconCompat.createWithBitmap(it) }
        val person = Person.Builder()
            .setName(messageDto.senderUsername)
            .setIcon(icon)
            .build()

        // Create shortcut
        val intent = Intent(appContext, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val shortcut = ShortcutInfoCompat.Builder(appContext, messageDto.senderUsername)
            .setLongLived(true)
            .setIntent(intent)
            .setShortLabel(messageDto.senderUsername)
            .setPerson(person)
            .setIcon(icon)
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(appContext, shortcut)

        // Create MessagingStyle
        val messagingStyle = NotificationCompat.MessagingStyle(person)

        // Add message
        val notificationMessage = NotificationCompat.MessagingStyle.Message(
            messageDto.content,
            System.currentTimeMillis(),
            person
        )
        messagingStyle.addMessage(notificationMessage)

        val publicVersion = NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle("New message")
            .setContentText("you got 3 new messages")
            .setColor(Color.RED)
            .setColorized(true)
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Create notification
        val notification = NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setStyle(messagingStyle)
            .setShortcutId(messageDto.senderUsername)
            .setColor(Color.RED)
            .setColorized(true)
            .setSmallIcon(R.drawable.app_logo)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicVersion)
            .build()

        return notification
    }

    fun createUploadingNewVideoNotification(currentProgress: Int, maxProgress: Int) : Notification {
        val notification = NotificationCompat.Builder(appContext, NotificationChannelInfo.UploadingNewVideo.id)
            .setContentTitle("Uploading Video")
            .setContentText("Your video is being uploaded...")
            .setSmallIcon(R.drawable.arrow_upward_24px)
            .setOngoing(true)
            .setProgress(maxProgress, currentProgress, false)
            .build()

        return notification
    }

    fun createUploadingNewVideoNotificationBuilder() : NotificationCompat.Builder {
        return NotificationCompat.Builder(appContext, NotificationChannelInfo.UploadingNewVideo.id)
            .setContentTitle("Uploading Video")
            .setContentText("Your video is being uploaded...")
            .setSmallIcon(R.drawable.arrow_upward_24px)
            .setOngoing(true)
    }

    fun createFinishUploadVideoFileNotificationBuilder(videoThumbnail: Bitmap?) : NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle("Upload video success")
            .setContentText("Your video has been uploaded. It is currently being processed.")
            .setSmallIcon(R.drawable.arrow_upward_24px)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        if (videoThumbnail != null) {
            return notificationBuilder.setLargeIcon(videoThumbnail)
        }

        return notificationBuilder
    }

    fun createUploadVideoErrorFileNotificationBuilder(videoThumbnail: Bitmap?) : NotificationCompat.Builder {
        val notificationBuilder = NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle("Upload failed")
            .setContentText("Video upload failed. An unexpected error occurred.")
            .setSmallIcon(R.drawable.arrow_upward_24px)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        if (videoThumbnail != null) {
            return notificationBuilder.setLargeIcon(videoThumbnail)
        }

        return notificationBuilder
    }

    fun createNewFollowerNotificationBuilder(decodedNotificationDto: NotificationModel) : NotificationCompat.Builder {
        val thumbnailBitmap = decodedNotificationDto.senderAvatarUrl?.let { getCircleBitmapFromURL(it) }
        return NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle("New follower")
            .setContentText(decodedNotificationDto.message)
            .setSmallIcon(R.drawable.app_logo)
            .setLargeIcon(thumbnailBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    fun createVideoLikeNotificationBuilder(decodedNotificationDto: NotificationModel) : NotificationCompat.Builder {
        val notificationData = decodedNotificationDto.data as VideoLikeNotificationData
        val thumbnailBitmap = getBitmapFromURL(notificationData.thumbnailUrl)
        return NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle(decodedNotificationDto.message)
            .setContentText(notificationData.videoTitle)
            .setSmallIcon(R.drawable.app_logo)
            .setLargeIcon(thumbnailBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    fun createMyCommentLikedNotificationBuilder(decodedNotificationDto: NotificationModel) : NotificationCompat.Builder {
        val notificationData = decodedNotificationDto.data as CommentLikeNotificationData
        val thumbnailBitmap = getCircleBitmapFromURL(notificationData.thumbnailUrl)
        return NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle(decodedNotificationDto.message)
            .setContentText(notificationData.commentContent)
            .setSmallIcon(R.drawable.app_logo)
            .setLargeIcon(thumbnailBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    fun createNewCommentOnMyVideoNotificationBuilder(decodedNotificationDto: NotificationModel) : NotificationCompat.Builder {
        val notificationData = decodedNotificationDto.data as CommentOnVideoNotificationData
        val thumbnailBitmap = getCircleBitmapFromURL(notificationData.thumbnailUrl)
        return NotificationCompat.Builder(appContext, NotificationChannelInfo.Message.id)
            .setContentTitle(decodedNotificationDto.message)
            .setContentText(notificationData.commentContent)
            .setSmallIcon(R.drawable.app_logo)
            .setLargeIcon(thumbnailBitmap)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
    }

    private fun Bitmap.toCircleBitmap(): Bitmap {
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

    private fun getCircleBitmapFromURL(src: String): Bitmap? {
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
