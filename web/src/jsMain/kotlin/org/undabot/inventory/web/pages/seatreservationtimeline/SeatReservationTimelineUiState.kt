package org.product.inventory.web.pages.seatreservationtimeline

import org.product.inventory.web.components.AlertMessage
import org.product.inventory.web.components.BreadcrumbItem
import seatreservation.model.Office

data class SeatReservationTimelineUiState(
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val title: String = "",
    val isLoading: Boolean = true,
    val alertMessage: AlertMessage? = null,
    val selectedOffice: Office? = null,
    val availableOffices: List<Office> = emptyList(),
    val reservableDateUiItems: List<ReservableDateUiItem> = emptyList(),
)
