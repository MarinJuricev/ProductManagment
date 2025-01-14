package org.product.inventory.web.pages.seatreservation

import org.product.inventory.web.components.BreadcrumbItem
import seatreservation.dashboard.model.SeatReservationOption

data class SeatReservationUiState(
    val title: String = "",
    val breadcrumbItems: List<BreadcrumbItem> = emptyList(),
    val routeToNavigate: String? = null,
    val seatReservationItems: List<SeatReservationItemUi> = emptyList(),
)

data class SeatReservationItemUi(
    val icon: String,
    val title: String,
    val text: String,
    val type: SeatReservationOption,
)
