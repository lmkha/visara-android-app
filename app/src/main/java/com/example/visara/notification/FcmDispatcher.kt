package com.example.visara.notification

import android.app.NotificationManager
import android.util.Log
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmDispatcher @Inject constructor(
    private val notificationManager: NotificationManager,
    private val notificationHelper: NotificationHelper,
) {
    fun dispatch(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("CHECK_VAR", "Message data payload: ${remoteMessage.data}")
        }
        CoroutineScope(Dispatchers.IO).launch {
            val notification = notificationHelper.createMessageNotification(remoteMessage)
            notificationManager.notify(123, notification)
        }
    }
}
