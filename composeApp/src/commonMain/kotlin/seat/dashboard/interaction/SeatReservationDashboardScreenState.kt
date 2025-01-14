package seat.dashboard.interaction

import seatreservation.dashboard.model.SeatReservationOption

sealed interface SeatReservationDashboardScreenState {
    data object Loading : SeatReservationDashboardScreenState
    data object Retry : SeatReservationDashboardScreenState
    data class Content(
        val options: List<SeatReservationOption>,
    ) : SeatReservationDashboardScreenState
}
