package com.example.visara.service.fcm

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
    lateinit var fcmDispatcher: FcmDispatcher

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        runBlocking(Dispatchers.IO) {
            appSettingsRepository.updateFCMToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        fcmDispatcher.dispatch(remoteMessage)
    }
}
