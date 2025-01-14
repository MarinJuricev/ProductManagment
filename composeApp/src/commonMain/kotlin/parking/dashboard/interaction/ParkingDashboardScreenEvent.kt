package parking.dashboard.interaction

import parking.dashboard.model.ParkingDashboardOption

sealed interface ParkingDashboardScreenEvent {
    data object RetryButtonClick : ParkingDashboardScreenEvent
    data class DashboardOptionSelect(val option: ParkingDashboardOption) :
        ParkingDashboardScreenEvent
}
