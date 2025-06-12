package com.example.visara.service.fcm

import android.util.Log
import com.example.visara.data.repository.AppSettingsRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject
    lateinit var appSettingsRepository: AppSettingsRepository
    @Inject
    lateinit var fcmProcessor: FcmProcessor

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        runBlocking(Dispatchers.IO) {
            appSettingsRepository.updateFCMToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.i("CHECK_VAR", "received fcm message, data = : ${remoteMessage.data}")
        fcmProcessor.process(remoteMessage)
    }
}
