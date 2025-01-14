package org.product.inventory.web.datetime

import core.utils.millisNow
import kotlinx.datetime.internal.JSJoda.DateTimeFormatter
import kotlinx.datetime.internal.JSJoda.Instant
import kotlinx.datetime.internal.JSJoda.LocalDate
import kotlinx.datetime.internal.JSJoda.LocalDateTime
import kotlinx.datetime.internal.JSJoda.ZoneId
import kotlin.js.Date

private const val DATE_TIME_PATTERN = "dd.MM.yyyy. HH:mm"
const val DATE_PATTERN = "dd.MM.yyyy."
const val ISO_DATE_PATTERN = "yyyy-MM-dd"

fun localDateNow() = millisNow().toLocalDate()

fun localDateOfLastDayInCurrentMonth(): LocalDate = millisNow()
    .toLocalDate()
    .plusMonths(1)
    .withDayOfMonth(1)
    .minusDays(1)

fun Long.toLocalDate(zoneId: ZoneId = ZoneId.SYSTEM): LocalDate = LocalDate.ofInstant(
    instant = Instant.ofEpochMilli(this.toDouble()),
    zoneId = zoneId,
)

fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.SYSTEM): LocalDateTime = LocalDateTime.ofInstant(
    instant = Instant.ofEpochMilli(this.toDouble()),
    zoneId = zoneId,
)

val LocalDate.millis: Long get() = Date(
    year = year(),
    month = monthValue() - 1,
    day = dayOfMonth(),
).getTime().toLong()

private val defaultDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
private val defaultDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)

fun LocalDate.formatted(
    formatter: DateTimeFormatter = defaultDateFormatter,
): String = format(formatter)

fun LocalDateTime.formatted(
    formatter: DateTimeFormatter = defaultDateTimeFormatter,
): String = format(formatter)

fun LocalDate.isBeforeOrEqual(other: LocalDate) = !isAfter(other)

fun LocalDate.isTodayOrAfter() = !isBefore(localDateNow())
