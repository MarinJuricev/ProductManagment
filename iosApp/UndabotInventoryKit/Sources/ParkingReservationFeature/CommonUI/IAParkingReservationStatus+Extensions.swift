import SwiftUI
import Utilities
import Shared

extension IAParkingReservationStatus {
    public var title: String {
        switch self {
        case .approved:
            MR.strings().parking_reservation_status_approved_label.desc().localized()
        case .cancelled:
            MR.strings().parking_reservation_status_canceled_label.desc().localized()
        case .declined:
            MR.strings().parking_reservation_status_rejected_label.desc().localized()
        case .submitted:
            MR.strings().parking_reservation_status_submitted_label.desc().localized()
        }
    }

    public var color: Color {
        switch self {
        case .approved:
            Color(resource: \.success)
        case .cancelled:
            Color(resource: \.textLight)
        case .declined:
            Color(resource: \.error)
        case .submitted:
            Color(resource: \.warning)
        }
    }
}
