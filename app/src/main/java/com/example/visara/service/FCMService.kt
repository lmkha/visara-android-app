package com.example.visara.service

import android.util.Log
import com.example.visara.data.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var fcmDispatcher: FcmDispatcher

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        authRepository.updateFcmToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("CHECK_VAR", "received FCM message")
        fcmDispatcher.dispatch(remoteMessage)
    }
}
