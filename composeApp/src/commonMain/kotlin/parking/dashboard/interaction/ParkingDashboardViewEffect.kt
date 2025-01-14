package parking.dashboard.interaction

sealed interface ParkingDashboardViewEffect {
    data object NavigateToNewRequest : ParkingDashboardViewEffect
    data object NavigateToUserRequests : ParkingDashboardViewEffect
    data object NavigateToCrewManagement : ParkingDashboardViewEffect
    data object NavigateToMyReservations : ParkingDashboardViewEffect
    data object NavigateToEmailTemplates : ParkingDashboardViewEffect
    data object NavigateToSlotsManagement : ParkingDashboardViewEffect
}
