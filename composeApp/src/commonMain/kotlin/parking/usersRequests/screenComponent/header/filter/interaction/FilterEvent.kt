package parking.usersRequests.screenComponent.header.filter.interaction

import parking.reservation.model.ParkingReservationStatusUiModel

sealed interface FilterEvent {
    data class DateRangeChanged(val startDate: Long, val endDate: Long) : FilterEvent
    data class SearchEmailChanged(val email: String) : FilterEvent
    data class StatusChanged(val status: ParkingReservationStatusUiModel?) : FilterEvent
}
