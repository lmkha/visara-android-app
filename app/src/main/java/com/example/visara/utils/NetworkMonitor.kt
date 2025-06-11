package com.example.visara.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(@ApplicationContext appContext: Context) {
    private val _isOnLine: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnLine.asStateFlow()

    private val connectivityManager = appContext.getSystemService(ConnectivityManager::class.java)
    private val callBack = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isOnLine.update { true }
        }

        override fun onUnavailable() {
            _isOnLine.update { false }
        }

        override fun onLost(network: Network) {
            _isOnLine.update { false }
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(callBack)
        val isConnected = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } == true

        _isOnLine.value = isConnected
    }
}