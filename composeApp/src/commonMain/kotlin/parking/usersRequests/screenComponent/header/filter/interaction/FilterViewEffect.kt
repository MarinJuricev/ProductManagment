package parking.usersRequests.screenComponent.header.filter.interaction

import parking.reservation.model.ParkingReservationStatusUiModel

sealed interface FilterViewEffect {
    data class DateRangeFilterApplied(val startDate: Long, val endDate: Long) :
        FilterViewEffect

    data class StatusFilterApplied(val status: ParkingReservationStatusUiModel?) : FilterViewEffect
    data class EmailFilterApplied(val email: String) : FilterViewEffect
}
