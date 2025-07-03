package com.example.visara.utils

import android.icu.text.CompactDecimalFormat
import android.icu.text.CompactDecimalFormat.CompactStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.visara.R
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun getTimeAgo(unixTimeString: String): String {
    if (unixTimeString.isEmpty()) return ""
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val dateTime = try {
        OffsetDateTime.parse(unixTimeString, formatter)
    } catch (e: Exception) {
        return ""
    }

    val currentTime = OffsetDateTime.now()
    val duration = Duration.between(dateTime, currentTime).abs()
    return when {
        duration.toDays() > 365 -> {
            val years = duration.toDays() / 365
            pluralStringResource(
                id = R.plurals.year_ago,
                count = years.toInt(),
                years.toString()
            )
        }
        duration.toDays() > 30 -> {
            val months = duration.toDays() / 30
            pluralStringResource(
                id = R.plurals.month_ago,
                count = months.toInt(),
                months
            )
        }
        duration.toDays() >= 7 -> {
            val weeks = duration.toDays() / 7
            pluralStringResource(
                id = R.plurals.week_ago,
                count = weeks.toInt(),
                weeks
            )
        }
        duration.toDays() >= 1 -> {
            val days = duration.toDays()
            pluralStringResource(
                id = R.plurals.day_ago,
                count = days.toInt(),
                days
            )
        }
        duration.toHours() >= 1 -> {
            val hours = duration.toHours()
            pluralStringResource(
                id = R.plurals.hour_ago,
                count = hours.toInt(),
                hours
            )
        }
        duration.toMinutes() >= 1 -> {
            val minutes = duration.toMinutes()
            pluralStringResource(
                id = R.plurals.minute_ago,
                count = minutes.toInt(),
                minutes
            )
        }
        else -> {
            stringResource(R.string.time_just_now)
        }
    }
}

@Composable
fun formatViews(value: Long): String {
    val formatter = CompactDecimalFormat.getInstance(
        LocalConfiguration.current.locales.get(0),
        CompactStyle.SHORT
    ).apply {
        maximumFractionDigits = 1
    }

    return pluralStringResource(
        id = R.plurals.view_count,
        count = value.toInt(),
        formatter.format(value)
    )
}

@Composable
fun formatCommentCount(value: Long): String {
    val formatter = CompactDecimalFormat.getInstance(
        LocalConfiguration.current.locales.get(0),
        CompactStyle.SHORT
    ).apply {
        maximumFractionDigits = 1
    }

    return pluralStringResource(
        id = R.plurals.comment_count,
        count = value.toInt(),
        formatter.format(value)
    )
}

@Composable
fun formatDuration(durationSeconds: Long): String {
    val hours = durationSeconds / 3600
    val minutes = (durationSeconds % 3600) / 60
    val seconds = durationSeconds % 60

    return if (hours > 0) {
        String.format(
            locale = LocalConfiguration.current.locales.get(0),
            format = "%02d:%02d:%02d", hours, minutes, seconds
        )
    } else {
        String.format(
            locale = LocalConfiguration.current.locales.get(0),
            format = "%02d:%02d", minutes, seconds
        )
    }
}
