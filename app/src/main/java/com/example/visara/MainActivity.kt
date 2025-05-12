package com.example.visara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.visara.ui.App
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val tag = "CHECK_VAR"

        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
                Log.i(tag, "The default network is now: $network")
            }

            override fun onUnavailable() {
                super.onUnavailable()
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
            }

            override fun onLost(network : Network) {
                Log.e(tag, "The application no longer has a default network. The last default network was $network")
            }
        })
        */
        enableEdgeToEdge()

        setContent {
            App()
        }
    }
}
