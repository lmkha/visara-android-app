package com.example.visara.ui.utils

import android.annotation.SuppressLint
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun String.toTimeAgo(): String {
    if (this.isEmpty()) return ""
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val dateTime = OffsetDateTime.parse(this, formatter)

    val currentTime = OffsetDateTime.now()

    val duration = Duration.between(dateTime, currentTime)

    return when {
        duration.toDays() > 365 -> {
            val years = duration.toDays() / 365
            if (years > 1) "$years years ago" else "1 year ago"
        }
        duration.toDays() > 30 -> {
            val months = duration.toDays() / 30
            if (months > 1) "$months months ago" else "1 month ago"
        }
        duration.toDays() > 7 -> {
            val weeks = duration.toDays() / 7
            if (weeks > 1) "$weeks weeks ago" else "1 week ago"
        }
        duration.toDays() > 1 -> {
            "${duration.toDays()} days ago"
        }
        duration.toHours() > 1 -> {
            "${duration.toHours()} hours ago"
        }
        duration.toMinutes() > 1 -> {
            "${duration.toMinutes()} minutes ago"
        }
        else -> {
            "Just now"
        }
    }
}


@SuppressLint("DefaultLocale")
fun formatViews(views: Long): String {
    return when {
        views >= 1_000_000_000 -> {
            String.format("%.1fB views", views / 1_000_000_000.0)
        }
        views >= 1_000_000 -> {
            String.format("%.1fM views", views / 1_000_000.0)
        }
        views >= 1_000 -> {
            String.format("%.1fK views", views / 1_000.0)
        }
        else -> {
            "$views views"
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatDuration(durationSeconds: Long): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
