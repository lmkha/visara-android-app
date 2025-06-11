package com.example.visara

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.content.getSystemService
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.visara.utils.AppLifecycleObserver
import com.example.visara.notification.NotificationChannelInfo
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.work.Configuration

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var appLifecycleObserver: AppLifecycleObserver

    override val workManagerConfiguration: Configuration get() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val notificationManager = getSystemService<NotificationManager>()!!
        NotificationChannelInfo.all.forEach { channelInfo ->
            val channel = channelInfo.let {
                NotificationChannel(it.id, it.name, it.importance)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
