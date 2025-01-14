package core.utils

import kotlinx.datetime.TimeZone
import platform.Foundation.NSTimeZone

class DateConverter {
    fun formatTimeZone(platformTimeZone: NSTimeZone): TimeZone = TimeZone.of(platformTimeZone.name)
}
