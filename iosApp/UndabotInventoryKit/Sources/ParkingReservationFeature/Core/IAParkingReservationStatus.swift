import CasePaths
import Shared

@CasePathable
@dynamicMemberLookup
public enum IAParkingReservationStatus: Equatable, Sendable {
    case submitted
    case approved(adminNote: String, parkingCoordinate: IAParkingCoordinate)
    case declined(adminNote: String)
    case cancelled

    public init(_ status: ParkingReservationStatus) {
        switch onEnum(of: status) {
        case .approved(let status):
            self = .approved(
                adminNote: status.adminNote,
                parkingCoordinate: IAParkingCoordinate(status.parkingCoordinate)
            )
        case .canceled:
            self = .cancelled
        case .declined(let status):
            self = .declined(adminNote: status.adminNote)
        case .submitted:
            self = .submitted
        }
    }

    public func toParkingReservationStatus() -> ParkingReservationStatus? {
        switch self {
        case .submitted:
            return .Submitted()
        case let .approved(adminNote, parkingCoordinate):
            return .Approved(
                adminNote: adminNote,
                parkingCoordinate: ParkingCoordinate(parkingCoordinate)
            )
        case .declined(adminNote: let adminNote):
            return .Declined(adminNote: adminNote)
        case .cancelled:
            return .Canceled()
        }
    }
}
