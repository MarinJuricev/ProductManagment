package seatreservation.dashboard

import seatreservation.dashboard.model.SeatReservationOption

sealed class SeatReservationDashboardType {
    data class Administrator(val options: List<SeatReservationOption>) : SeatReservationDashboardType()
    data object User : SeatReservationDashboardType()
}
