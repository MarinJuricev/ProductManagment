package org.product.inventory.web.pages.seatreservationtmanagement

data class SeatReservationManagementEvents(
    val isLoading: Boolean = true,
    val submitActive: Boolean = false,
    val routeToNavigate: String? = null,
    val deleteInProgress: Boolean = false,
    val closeDeleteDialog: Boolean = false,
    val closeEditDialog: Boolean = false,
)
