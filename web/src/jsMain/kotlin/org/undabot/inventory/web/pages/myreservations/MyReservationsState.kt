package org.product.inventory.web.pages.myreservations

import org.product.inventory.web.components.BreadcrumbItem
import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.toDateInputValue
import org.product.inventory.web.datetime.localDateOfLastDayInCurrentMonth

data class MyReservationsState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val reservations: List<ParkingReservationItemUi> = emptyList(),
    val fromDateLabel: String = "",
    val toDateLabel: String = "",
    val fromDate: DateInputValue = DateInputValue(),
    val toDate: DateInputValue = localDateOfLastDayInCurrentMonth().toDateInputValue(),
    val emptyReservationsMessage: String = "",
    val isLoading: Boolean = false,
    val routeToNavigate: String? = null,
)
