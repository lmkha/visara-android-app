package com.example.visara.data.repository

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InboxRepository @Inject constructor(

) {
    fun sayOk() {
        Log.i("CHECK_VAR", "InboxRepository Ok")
    }
}
