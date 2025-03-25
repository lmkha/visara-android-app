package com.example.datn_mobile.test

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Base64

@RequiresApi(Build.VERSION_CODES.O)
fun main() {
    val token: String = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJmY2JhcmNlbG9uYSIsImlhdCI6MTc0MjgxODQzNSwiZXhwIjoxNzQyODYxNjM1fQ.svvP7yWg-xTBWeTiEvNGS5FmngouUedCXMaklAX1n1jZTKDeYQA_iISMDBR6GzE8"
    val parts = token.split(".")
    val header = String(Base64.getUrlDecoder().decode(parts[0]))
    val payload = String(Base64.getUrlDecoder().decode(parts[1]))
    val signature = String(Base64.getUrlDecoder().decode(parts[2]))
    println(header)
    println(payload)
    println(signature)
}
