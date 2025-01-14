package parking.dashboard.interaction

import parking.dashboard.model.ParkingDashboardOption

sealed interface ParkingDashboardScreenState {
    data class Content(
        val items: List<ParkingDashboardOption> = listOf(),
    ) : ParkingDashboardScreenState

    data object Loading : ParkingDashboardScreenState
    data class Retry(
        val title: String,
        val description: String,
        val buttonText: String,
    ) : ParkingDashboardScreenState
}
