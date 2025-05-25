package com.example.visara.notification

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.visara.common.AppLifecycleObserver
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmDispatcher @Inject constructor(
    private val notificationManager: NotificationManager,
    private val notificationHelper: NotificationHelper,
    private val appLifecycleObserver: AppLifecycleObserver,
    @ApplicationContext private val appContext: Context,
) {
    fun dispatch(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("CHECK_VAR", "Message data payload: ${remoteMessage.data}")
        }
        CoroutineScope(Dispatchers.IO).launch {
            val notification = notificationHelper.createMessageNotification(remoteMessage)
            if (!appLifecycleObserver.isAppInForeground) {
                notificationManager.notify(123, notification)
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(appContext, "You have a new message", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
