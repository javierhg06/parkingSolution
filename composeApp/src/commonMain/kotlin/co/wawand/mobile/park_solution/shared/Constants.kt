package co.wawand.mobile.park_solution.shared

import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

object Constants {
    const val WEB_CLIENT_ID = "351048985706-kd6vc0rqoahen17hmas28ar6h9ssrer0.apps.googleusercontent.com"
}

val DefaultTimeFormat = LocalTime.Format {
    hour(padding = Padding.SPACE)
    char(value = ':')
    minute(padding = Padding.SPACE)
    // Uncomment if needed
    // char(' ')
    // amPmMarker("AM", "PM")
}