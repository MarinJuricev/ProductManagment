package org.product.inventory.web.pages.usersrequests

import org.product.inventory.web.components.DateInputValue
import org.product.inventory.web.components.ParkingOption
import org.product.inventory.web.pages.myreservations.ParkingReservationStatusUi

sealed interface UserRequestsEvent {

    data class DateRangeChanged(
        val fromDate: DateInputValue? = null,
        val toDate: DateInputValue? = null,
    ) : UserRequestsEvent

    data class StatusChanged(
        val status: ParkingReservationStatusUi?,
        val itemId: String,
    ) : UserRequestsEvent

    data class ItemClick(val itemId: String) : UserRequestsEvent

    data object SaveClick : UserRequestsEvent

    data object ReFetchGarageLevelsClick : UserRequestsEvent

    data class ApproveNoteChanged(val text: String) : UserRequestsEvent

    data class RejectReasonChanged(val text: String) : UserRequestsEvent

    data class GarageLevelChanged(val parkingOption: ParkingOption) : UserRequestsEvent

    data class ParkingSpotChanged(val parkingOption: ParkingOption) : UserRequestsEvent

    data object DetailsClosed : UserRequestsEvent

    data class PathClick(val path: String) : UserRequestsEvent

    data object TogglePermanentGarageAccess : UserRequestsEvent

    data class EmailFilterChanged(val email: String) : UserRequestsEvent

    data class StatusFilterChanged(val status: ParkingReservationStatusUi?) : UserRequestsEvent
}
