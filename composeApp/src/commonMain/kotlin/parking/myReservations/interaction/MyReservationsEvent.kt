package parking.myReservations.interaction

import parking.reservation.model.ParkingReservationUiModel

sealed interface MyReservationsEvent {
    data class ReservationClick(val reservation: ParkingReservationUiModel) : MyReservationsEvent
    data class DateRangeSelect(val startDate: Long, val endDate: Long) : MyReservationsEvent
    data object ReservationUpdated : MyReservationsEvent
    data object RetryClick : MyReservationsEvent
}
