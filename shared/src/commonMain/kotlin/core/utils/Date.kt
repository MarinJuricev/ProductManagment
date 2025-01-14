package core.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

fun Long.isFutureDate(
    clock: Clock = Clock.System,
    timezone: TimeZone = TimeZone.currentSystemDefault(),
): Boolean {
    val currentDate = clock.now().toLocalDateTime(timezone).date
    val givenDate = this.toLocalDate(timezone)
    return currentDate < givenDate
}

fun Long.toLocalDate(
    timezone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDate = Instant
    .fromEpochMilliseconds(this)
    .toLocalDateTime(timezone).date

fun Long.startOfTheDay(
    timezone: TimeZone = TimeZone.currentSystemDefault(),
): Double =
    toLocalDate(timezone)
        .atStartOfDayIn(timezone)
        .toEpochMilliseconds()
        .toDouble()

fun Long.endOfTheDay(
    timezone: TimeZone = TimeZone.currentSystemDefault(),
): Double =
    toLocalDate(timezone)
        .atStartOfDayIn(timezone)
        .plus(1, DateTimeUnit.DAY, timezone)
        .minus(1, DateTimeUnit.SECOND)
        .toEpochMilliseconds()
        .toDouble()

fun Long.isNotLateReservation(
    clock: Clock = Clock.System,
    timezone: TimeZone = TimeZone.currentSystemDefault(),
): Boolean {
    val currentDateTime = clock.now().toLocalDateTime(timezone)
    val date = Instant
        .fromEpochMilliseconds(this)
        .toLocalDateTime(timezone).date

    val tomorrowDate = currentDateTime.date.plus(1, DateTimeUnit.DAY)
    val threeOClockToday = currentDateTime.date.atTime(15, 0)

    return when {
        date == tomorrowDate -> currentDateTime < threeOClockToday
        else -> true
    }
}

// returns non-localized day name from LocalDate
fun LocalDate.dayFormatted() = dayOfWeek.name.lowercase().replaceFirstChar(Char::uppercase)
