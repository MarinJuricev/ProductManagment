package parking.usersRequests.interaction

import parking.reservation.model.ParkingReservationStatusUiModel
import parking.reservation.model.ParkingReservationUiModel

sealed interface UsersRequestsEvent {
    data class ReservationClick(val reservation: ParkingReservationUiModel) : UsersRequestsEvent
    data class DateRangeSelect(val startDate: Long, val endDate: Long) : UsersRequestsEvent
    data object ParkingReservationUpdate : UsersRequestsEvent
    data class EmailFilterApplied(val email: String) : UsersRequestsEvent
    data class StatusFilterApplied(val status: ParkingReservationStatusUiModel?) : UsersRequestsEvent
}
