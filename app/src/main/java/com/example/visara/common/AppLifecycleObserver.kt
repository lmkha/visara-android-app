package com.example.visara.common

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {
    var isAppInForeground: Boolean = false
        private set

    override fun onStart(owner: LifecycleOwner) {
        isAppInForeground = true
        super.onStart(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        isAppInForeground = false
        super.onStop(owner)
    }
}
