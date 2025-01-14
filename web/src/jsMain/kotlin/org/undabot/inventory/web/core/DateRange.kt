package org.product.inventory.web.core

import kotlinx.datetime.internal.JSJoda.LocalDate
import org.product.inventory.web.datetime.localDateNow
import org.product.inventory.web.datetime.localDateOfLastDayInCurrentMonth

data class DateRange(
    val fromDate: LocalDate = localDateNow(),
    val toDate: LocalDate = localDateOfLastDayInCurrentMonth(),
)
