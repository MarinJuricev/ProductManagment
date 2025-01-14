package seat.management.interaction

sealed interface SeatManagementViewEffect {
    data object OpenTimeline : SeatManagementViewEffect
    data class ShowMessage(val message: String) : SeatManagementViewEffect
}
