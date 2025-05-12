package com.example.visara.di

import android.content.Context
import com.example.visara.device.NetworkMonitor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SystemStatusMonitorModule {
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext appContext: Context) : NetworkMonitor {
        return NetworkMonitor(appContext)
    }
}
