import IdentifiedCollections
import Shared

public enum IASeatReservationDashboardType: Equatable, Sendable {
    case administrator(IdentifiedArrayOf<IASeatReservationOption>)
    case user

    public init(_ seatReservationDashboardType: SeatReservationDashboardType) {
        switch onEnum(of: seatReservationDashboardType) {
        case .administrator(let administrator):
            self = .administrator(IdentifiedArray(uniqueElements: administrator.options.map { IASeatReservationOption($0) }))
        case .user:
            self = .user
        }
    }
}
