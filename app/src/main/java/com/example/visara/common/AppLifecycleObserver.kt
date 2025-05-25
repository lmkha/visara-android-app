package com.example.visara.common

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject

class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {
    var isAppInForeground: Boolean = false
        private set

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("CHECK_VAR", "App in foreground")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("CHECK_VAR", "App in background")
    }
}
