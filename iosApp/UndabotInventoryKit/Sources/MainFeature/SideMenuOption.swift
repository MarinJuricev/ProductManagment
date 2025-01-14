import SwiftUI
import Utilities
import Shared

public enum SideMenuOption: CaseIterable, Identifiable, Equatable, Sendable {
    case testDevices
    case parkingReservation
    case seatReservation
    case crewManagement

    public var id: Self {
        self
    }

    init(_ menuOption: MenuOption) {
        switch menuOption {
        case .testDevices:
            self = .testDevices
        case .parkingReservations:
            self = .parkingReservation
        case .seatReservation:
            self = .seatReservation
        case .crewManagement:
            self = .crewManagement
        }
    }

    var title: String {
        switch self {
        case .parkingReservation:
            return MR.strings().side_menu_parking_reservation_title.desc().localized()

        case .testDevices:
            return MR.strings().side_menu_test_devices_title.desc().localized()

        case .seatReservation:
            return MR.strings().side_menu_seat_reservation_title.desc().localized()

        case .crewManagement:
            return MR.strings().parking_reservation_crew_management_title.desc().localized()
        }
    }

    var icon: Image {
        switch self {
        case .parkingReservation:
            return Image(resource: \.parking_reservation_icon)
        case .testDevices:
            return Image(resource: \.test_devices_icon)
        case .seatReservation:
            return Image(resource: \.seat_reservation_icon)
        case .crewManagement:
            return Image(resource: \.crew_management_icon)
        }
    }
}
