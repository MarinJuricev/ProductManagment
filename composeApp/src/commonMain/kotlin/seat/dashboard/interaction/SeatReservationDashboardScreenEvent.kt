package seat.dashboard.interaction

import seatreservation.dashboard.model.SeatReservationOption

sealed interface SeatReservationDashboardScreenEvent {
    data object RetryClick : SeatReservationDashboardScreenEvent
    data class AdminDashboardOptionClick(
        val option: SeatReservationOption,
    ) : SeatReservationDashboardScreenEvent
}
