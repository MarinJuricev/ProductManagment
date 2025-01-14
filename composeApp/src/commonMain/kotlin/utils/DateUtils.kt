package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun convertMillisToDate(millis: Long): String =
    SimpleDateFormat("dd.MM.yyyy.", Locale.getDefault())
        .format(Date(millis))

fun getMillisOfTomorrow(
    timezone: TimeZone = TimeZone.UTC,
    clock: Clock = Clock.System,
): Long {
    val now = clock.now()
    val today = now.toLocalDateTime(timezone).date
    val tomorrow = today.plus(1, DateTimeUnit.DAY)
    return tomorrow.atStartOfDayIn(timezone).toEpochMilliseconds()
}

fun getMillisOfFirstDayInCurrentMonth(): Long {
    val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val firstDateOfCurrentMonth = LocalDate(currentDate.year, currentDate.month, 1)
    val firstDateInstant = firstDateOfCurrentMonth.atStartOfDayIn(TimeZone.UTC)
    return firstDateInstant.toEpochMilliseconds()
}

fun getMillisOfLastDayInCurrentMonth(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    return calendar.timeInMillis
}

fun Long.isTodayOrAfter(
    timeZone: TimeZone = TimeZone.UTC,
    currentDate: LocalDate = Clock.System.now().toLocalDateTime(timeZone).date,
): Boolean {
    val givenDate = Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone).date
    return givenDate >= currentDate
}
