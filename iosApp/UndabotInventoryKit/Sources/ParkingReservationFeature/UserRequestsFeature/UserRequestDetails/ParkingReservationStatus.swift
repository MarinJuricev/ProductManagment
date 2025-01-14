import CasePaths
import Shared
import Core
import SwiftUI

extension UserRequestDetailsFeature {
    @CasePathable
    @dynamicMemberLookup
    public enum IAFlexibleParkingReservationStatus: Equatable, Sendable {
        case submitted
        case approved(adminNote: String, parkingCoordinate: IAParkingCoordinate?)
        case declined(adminNote: String)
        case cancelled

        public init(_ parkingReservationStatus: IAParkingReservationStatus) {
            switch parkingReservationStatus {
            case let .approved(adminNote, parkingCoordinate):
                self = .approved(
                    adminNote: adminNote,
                    parkingCoordinate: parkingCoordinate
                )
            case .cancelled:
                self = .cancelled
            case .declined(let adminNote):
                self = .declined(adminNote: adminNote)
            case .submitted:
                self = .submitted
            }
        }

        public func toParkingReservationStatus() -> IAParkingReservationStatus? {
            switch self {
            case .submitted:
                return .submitted
            case .approved(adminNote: let adminNote, parkingCoordinate: let parkingCoordinate):
                guard let parkingCoordinate else { return nil }
                return .approved(
                    adminNote: adminNote,
                    parkingCoordinate: parkingCoordinate
                )
            case .declined(adminNote: let adminNote):
                return .declined(adminNote: adminNote)
            case .cancelled:
                return .cancelled
            }
        }

        public func toParkingReservationStatusType() -> IAParkingReservationStatusType {
            switch self {
            case .cancelled:
                return .cancelled
            case .submitted:
                return .submitted
            case .approved:
                return .approved
            case .declined:
                return .declined
            }
        }

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
}
