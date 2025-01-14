package org.product.inventory.web.pages.myreservations

import org.product.inventory.web.components.DateInputValue

sealed interface MyReservationsEvent {

    data class DateRangeChanged(
        val fromDate: DateInputValue? = null,
        val toDate: DateInputValue? = null,
    ) : MyReservationsEvent

    data class ItemClick(val itemId: String) : MyReservationsEvent

    data object CancelReservationClick : MyReservationsEvent

    data class PathClick(val path: String) : MyReservationsEvent

    data class CancelConfirmClick(val itemId: String) : MyReservationsEvent

    data object CancelCanceledClick : MyReservationsEvent

    data object CloseDialogClick : MyReservationsEvent

    data object DetailsClosed : MyReservationsEvent
}
