package seat.dashboard.interaction

import cafe.adriel.voyager.core.screen.Screen

sealed interface SeatReservationDashboardViewEffect {
    data object OpenTimelineAsUser : SeatReservationDashboardViewEffect
    data class OpenDashboardOption(val screen: Screen) : SeatReservationDashboardViewEffect
}
