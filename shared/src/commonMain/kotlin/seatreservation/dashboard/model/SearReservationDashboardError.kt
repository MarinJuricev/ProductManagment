package seatreservation.dashboard.model

import core.model.MarinJuricevError

sealed class SearReservationDashboardError : MarinJuricevError() {
    data object ProfileNotFound : SearReservationDashboardError()
}
