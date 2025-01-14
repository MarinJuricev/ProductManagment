package parking.dashboard.model

import core.model.MarinJuricevError

sealed class ParkingDashboardError : MarinJuricevError() {
    data object ProfileNotFound : ParkingDashboardError()
}
