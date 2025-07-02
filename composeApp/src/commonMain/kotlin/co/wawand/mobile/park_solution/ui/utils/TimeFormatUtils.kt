package co.wawand.mobile.park_solution.ui.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant?.formatToHourMinute(): String {
    if (this == null) return "Unknown"

    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = localDateTime.hour
    val minute = localDateTime.minute

    val amPm = if (hour < 12) "AM" else "PM"
    val hour12 = if (hour % 12 == 0) 12 else hour % 12

    val hourStr = if (hour12 < 10) "0$hour12" else "$hour12"
    val minuteStr = if (minute < 10) "0$minute" else "$minute"

    return "$hourStr:$minuteStr $amPm"
}