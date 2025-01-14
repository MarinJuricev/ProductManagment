import ParkingReservationFeature
import SeatReservationFeature
import CrewManagementFeature

extension MainFeature.State {
    mutating func handleSideMenuOptionsChange(_ newSideMenuOptions: [SideMenuOption]) {
        guard newSideMenuOptions.contains(selectedMenuFeature.toSideMenuOption()) == false else {
            return
        }
        selectedMenuOption = .parkingReservation
        handleSideMenuOptionChange()
    }

    mutating func handleSideMenuOptionChange() {
        switch selectedMenuOption {
        case .testDevices:
            self.selectedMenuFeature = .testDevices
        case .parkingReservation:
            self.selectedMenuFeature = .parkingReservation(ParkingReservationDashboardFeature.State(user: user))
        case .seatReservation:
            self.selectedMenuFeature = .seatReservation(SeatReservationMainFeature.State(user: user))
        case .crewManagement:
            self.selectedMenuFeature = .crewManagement(CrewManagementFeature.State())
        }
    }
}

extension MainFeature.InventoryAppFeature.State {
    public func toSideMenuOption() -> SideMenuOption {
        switch self {
        case .testDevices:
            return .testDevices
        case .parkingReservation:
            return .parkingReservation
        case .seatReservation:
            return .seatReservation
        case .crewManagement:
            return .crewManagement
        }
    }
}
